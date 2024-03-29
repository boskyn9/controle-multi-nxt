package ch.aplu.nxtsim;

import ch.aplu.jgamegrid.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JOptionPane;

/**
 * Created by IntelliJ IDEA.
 * User: boskyn9
 * Date: 30/11/11
 * Time: 23:42
 * To change this template use File | Settings | File Templates.
 */
public class Nxt extends Actor
        implements GGActorCollisionListener, GGExitListener {

    /**
     * Center of a circle to detect robot-obstacle collisions
     * (pixel coordinates relative to center of robot image, default: (-13, 0)).
     */
    public static Point collisionCenter = new Point(0, 0);
    /**
     * Radius of a circle to detect robot-obstacle collisions
     * (in pixels, default: 20).
     */
    public static int collisionRadius = 16;
    private int nbObstacles = 0;
    private String title = "";
    private double rotInc;
    private int currentSpeed;
    private MotorState oldMotorStateA = MotorState.STOPPED;
    private MotorState oldMotorStateB = MotorState.STOPPED;
    private int oldSpeedA = -1;
    private int oldSpeedB = -1;
    private boolean isRotationInit = true;
    private int wheelDistance;
    private Gear.GearState oldGearState = Gear.GearState.STOPPED;
    private Point rotCenter;
    private GGVector pos;
    private double dir;
    private int sign;
    double oldRadius;
    double dphi;
    private GameGrid gg;
    private NxtRobot robot;
    
    
    public Nxt(Location startLocation, double startDirection, GameGrid gameGrid, NxtRobot robot) {
        super(true, "sprites/nxtrobot.gif");  // Rotatable
        this.gg = gameGrid;
        this.robot = robot;
        
        gg.addActor(this, startLocation, startDirection);
                
        this.getBackground().setFont(new Font ("TimesRoman", Font.BOLD, 12));
        this.getBackground().setPaintColor(Color.BLACK);
        
        pos = new GGVector(getLocation().x, getLocation().y); // Double coordinates
        
        wheelDistance = getHeight(0) - 10;

        addActorCollisionListener(this);
//        setCollisionCircle(collisionCenter, collisionRadius);
        setCollisionRectangle(collisionCenter, 40 , 40);
        
        
        gg.addExitListener(this);
        gg.show();
        if (NxtContext.isRun) {
            gg.doRun();
        }
        Class appClass = null;
        try {
            appClass = Class.forName(new Throwable().getStackTrace()[3].getClassName());
        } catch (Exception ex) {
        }
        exec(appClass, gg, "_init");
    }

    @Override
    public int collide(Actor actor1, Actor actor2) {
        System.out.println("collide");                
        return 0;
    }

    @Override
    public boolean notifyExit() {
        robot.exit();
        return true;
    }
    
    private void exec(Class appClass, GameGrid gg, String methodName) {
        Method execMethod = null;

        Method methods[] = appClass.getDeclaredMethods();
        for (int i = 0; i < methods.length; ++i) {
            if (methodName.equals(methods[i].getName())) {
                execMethod = methods[i];
                break;
            }
        }
        if (execMethod == null) {
            return;
        }

        execMethod.setAccessible(true);
        try {
            execMethod.invoke(this, new Object[]{
                        gg
                    });
        } catch (IllegalAccessException ex) {
//        System.out.println("method not accessible");
        } catch (IllegalArgumentException ex) {
            //       System.out.println("wrong parameter signature");
        } catch (InvocationTargetException ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.getTargetException().printStackTrace(pw);
            JOptionPane.showMessageDialog(null, sw.toString()
                    + "\n\nApplication will terminate.", "Fatal Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    public void reset() {
        pos = new GGVector(getLocationStart().x, getLocationStart().y); // Double coordinates
    }

    public void act() {
        
        synchronized (NxtRobot.class) {
            if (!title.equals("")) {
                title = "";
                gg.setTitle("");
            }
            
            
            //exibir o label no simulador
            showbtName();            
            
            // Add new obstacles as collision actor
            int nb = NxtContext.obstacles.size();
            if (nb > nbObstacles) {
                for (int i = nb - 1; i >= nbObstacles; i--) {
                    addCollisionActor(NxtContext.obstacles.get(i));
                }
                nbObstacles = nb;
            }

            // ------------------ We have a LightListener -------------
            for (Part part : robot.getParts()) {
                if (part instanceof LightSensor) {
                    ((LightSensor) part).notifyEvent();
                }
            }

            Gear gear = (Gear) (gg.getOneActorAt(getLocation(),Gear.class));
            ArrayList<Actor> motors = gg.getActors(Motor.class);
            if (gear != null && !motors.isEmpty()) {
                robot.fail("Error constructing NxtRobot" + "\nCannot add both Gear and Motor." + "\nApplication will terminate.");
            }

            // ------------------ We have a gear --------------------
            if (gear != null) {
                
                if(isNearBorder()){
                    try {
                        System.out.println("border");
                        gear.stop();
                        Thread.sleep(1000l);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Nxt.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                int speed = gear.getSpeed();
                if (currentSpeed != speed) {
                    setCurrentSpeed(speed);
                }
                if (speed == 0) {
                    return;
                }
                Gear.GearState state = gear.getState();
                double radius = gear.getRadius();
                if (state != oldGearState || radius != oldRadius) // State change
                {
                    oldGearState = state;
                    oldRadius = radius;
                    if (state == state.LEFT) {
                        initRot(-Math.abs(radius));
                        dphi = -SharedConstants.gearRotIncFactor * speed / radius;
                    }
                    if (state == state.RIGHT) {
                        initRot(Math.abs(radius));
                        dphi = SharedConstants.gearRotIncFactor * speed / radius;
                    }
                }
                switch (state) {
                    case FORWARD:
                        advance(SharedConstants.nbSteps);
                        break;
                    case BACKWARD:
                        advance(-SharedConstants.nbSteps);
                        break;
                    case LEFT:
                        if (gear.getRadius() == 0) {
                            turn(-SharedConstants.gearTurnAngle);
                        } else {
                            pos = getRotatedPosition(pos, rotCenter, dphi);
                            setLocation(new Location((int) (pos.x), (int) (pos.y)));
                            dir += dphi;
                            setDirection(dir);
                        }
                        break;
                    case RIGHT:
                        if (gear.getRadius() == 0) {
                            turn(SharedConstants.gearTurnAngle);
                        } else {
                            pos = getRotatedPosition(pos, rotCenter, dphi);
                            setLocation(new Location((int) (pos.x), (int) (pos.y)));
                            dir += dphi;
                            setDirection(dir);
                        }
                        break;
                }
            }

            // ------------------ We have a two motors --------------
            if (!motors.isEmpty() && motors.size() == 2) {
                Motor motorA = (Motor) motors.get(0);
                Motor motorB = (Motor) motors.get(1);
                int speedA = motorA.getSpeed();
                int speedB = motorB.getSpeed();
                if (speedA == 0 && speedB == 0) {
                    return;
                }
                MotorState stateA = motorA.getState();
                MotorState stateB = motorB.getState();
                double radius;

                if (stateA != oldMotorStateA || stateB != oldMotorStateB || speedA != oldSpeedA || speedB != oldSpeedB) // State change
                {
                    oldMotorStateA = stateA;
                    oldMotorStateB = stateB;
                    oldSpeedA = speedA;
                    oldSpeedB = speedB;
                    setCurrentSpeed((speedA + speedB) / 2);
                    isRotationInit = true;
                }

                if (stateA == MotorState.FORWARD && stateB == MotorState.FORWARD) {
                    if (speedA == speedB) {
                        advance(SharedConstants.nbSteps);
                    } else {
                        if (isRotationInit) {
                            isRotationInit = false;
                            sign = (speedA > speedB ? -1 : 1);
                            radius = wheelDistance / 2.0 * (speedA + speedB) / Math.abs(speedB - speedA);
                            initRot(sign * radius);
                            rotInc = SharedConstants.motorRotIncFactor * (speedA + speedB) / radius;
                        }
                        double rot = sign * rotInc;
                        pos = getRotatedPosition(pos, rotCenter, rot);
                        setLocation(new Location((int) (pos.x), (int) (pos.y)));
                        dir += rot;
                        setDirection(dir);
                    }
                }
                if (stateA == MotorState.BACKWARD && stateB == MotorState.BACKWARD) {
                    if (speedA == speedB) {
                        advance(-SharedConstants.nbSteps);
                    } else {
                        if (isRotationInit) {
                            isRotationInit = false;
                            sign = (speedA > speedB ? -1 : 1);
                            radius = wheelDistance / 2.0 * (speedA + speedB) / Math.abs(speedA - speedB);
                            initRot(sign * radius);
                            rotInc = SharedConstants.motorRotIncFactor * (speedA + speedB) / radius;
                        }
                        double rot = -sign * rotInc;
                        pos = getRotatedPosition(pos, rotCenter, rot);
                        setLocation(new Location((int) (pos.x), (int) (pos.y)));
                        dir += rot;
                        setDirection(dir);
                    }
                }
                if (stateA == MotorState.FORWARD && stateB == MotorState.BACKWARD) {
                    if (speedA == speedB) {
                        turn(-(int) (speedA / 60.0 * SharedConstants.motTurnAngle));
                    } else {
                        if (isRotationInit) {
                            isRotationInit = false;
                            sign = (speedA > speedB ? -1 : 1);
                            radius = wheelDistance / 200.0 * Math.abs(speedA - speedB);
                            initRot(sign * radius);
                            rotInc = SharedConstants.motorRotIncFactor * Math.max(speedA, speedB) / (wheelDistance + radius);
                        }
                        double rot = -rotInc;
                        pos = getRotatedPosition(pos, rotCenter, rot);
                        setLocation(new Location((int) (pos.x), (int) (pos.y)));
                        dir += rot;
                        setDirection(dir);
                    }
                }
                if (stateA == MotorState.BACKWARD && stateB == MotorState.FORWARD) {
                    if (speedA == speedB) {
                        turn((int) (speedA / 60.0 * SharedConstants.motTurnAngle));
                    } else {
                        if (isRotationInit) {
                            isRotationInit = false;
                            sign = (speedA > speedB ? -1 : 1);
                            radius = wheelDistance / 200.0 * Math.abs(speedA - speedB);
                            initRot(sign * radius);
                            rotInc = SharedConstants.motorRotIncFactor * Math.max(speedA, speedB) / (wheelDistance - Math.abs(radius));
                        }
                        double rot = rotInc;
                        pos = getRotatedPosition(pos, rotCenter, rot);
                        setLocation(new Location((int) (pos.x), (int) (pos.y)));
                        dir += rot;
                        setDirection(dir);
                    }
                }
                if (stateA == MotorState.FORWARD && stateB == MotorState.STOPPED) {
                    if (isRotationInit) {
                        isRotationInit = false;
                        radius = wheelDistance / 2;
                        initRot(-radius);
                        rotInc = SharedConstants.motorRotIncFactor * speedA / radius;
                    }
                    double rot = -rotInc;
                    pos = getRotatedPosition(pos, rotCenter, rot);
                    setLocation(new Location((int) (pos.x), (int) (pos.y)));
                    dir += rot;
                    setDirection(dir);
                }
                if (stateA == MotorState.BACKWARD && stateB == MotorState.STOPPED) {
                    if (isRotationInit) {
                        isRotationInit = false;
                        radius = wheelDistance / 2;
                        initRot(-radius);
                        rotInc = SharedConstants.motorRotIncFactor * speedA / radius;
                    }
                    double rot = rotInc;
                    pos = getRotatedPosition(pos, rotCenter, rot);
                    setLocation(new Location((int) (pos.x), (int) (pos.y)));
                    dir += rot;
                    setDirection(dir);
                }
                if (stateA == MotorState.STOPPED && stateB == MotorState.FORWARD) {
                    if (isRotationInit) {
                        isRotationInit = false;
                        radius = wheelDistance / 2;
                        initRot(radius);
                        rotInc = SharedConstants.motorRotIncFactor * speedB / radius;
                    }
                    double rot = rotInc;
                    pos = getRotatedPosition(pos, rotCenter, rot);
                    setLocation(new Location((int) (pos.x), (int) (pos.y)));
                    dir += rot;
                    setDirection(dir);
                }
                if (stateA == MotorState.STOPPED && stateB == MotorState.BACKWARD) {
                    if (isRotationInit) {
                        isRotationInit = false;
                        radius = wheelDistance / 2;
                        initRot(radius);
                        rotInc = SharedConstants.motorRotIncFactor * speedB / radius;
                    }
                    double rot = -rotInc;
                    pos = getRotatedPosition(pos, rotCenter, rot);
                    setLocation(new Location((int) (pos.x), (int) (pos.y)));
                    dir += rot;
                    setDirection(dir);
                }
            }
        }        
    }

    private void advance(int nbSteps) {
        pos = pos.add(
                new GGVector(nbSteps * Math.cos(Math.toRadians(getDirection())),
                nbSteps * Math.sin(Math.toRadians(getDirection()))));
        setLocation(new Location((int) (pos.x + 0.5), (int) (pos.y + 0.5)));
    }

    private void initRot(double radius) {
        GGVector v = new GGVector(getLocation().x, getLocation().y);
        GGVector vDir = new GGVector(
                -Math.sin(Math.toRadians(getDirection())),
                +Math.cos(Math.toRadians(getDirection())));
        GGVector vCenter = v.add(vDir.mult(radius));
        rotCenter = new Point((int) vCenter.x, (int) vCenter.y);
        pos = new GGVector(getLocation().x, getLocation().y);
        dir = getDirection();
    }

    public void turn(double angle) {
        synchronized (NxtRobot.class) {
            super.turn(angle);
            for (Part p : robot.getParts()) {
                p.turn(angle);
                p.setLocation(getPartLocation(p));
            }
        }
    }

    public void setLocation(Location loc) {
        synchronized (NxtRobot.class) {
            super.setLocation(loc);
            for (Part p : robot.getParts()) {
                p.setLocation(getPartLocation(p));
            }
        }
    }

    public void setDirection(double dir) {
        synchronized (NxtRobot.class) {
            super.setDirection(dir);
            for (Part p : robot.getParts()) {
                p.setLocation(getPartLocation(p));
                p.setDirection(dir);
            }
        }
    }

    public Location getPartLocation(Part part) {
        Location pos = part.getPosition();
        double r = Math.sqrt(pos.x * pos.x + pos.y * pos.y);
        double phi = Math.atan2(pos.y, pos.x);
        double dir = getDirection() * Math.PI / 180;
        Location loc = new Location(
                (int) (Math.round(getX() + r * Math.cos(dir + phi))),
                (int) (Math.round(getY() + r * Math.sin(dir + phi))));
        return loc;
    }

    private void setCurrentSpeed(int speed) {
        if (speed < 0) {
            speed = 0;
        }
        if (speed > 100) {
            speed = 100;
        }
        currentSpeed = speed;

        // period = m * speed + n;
        // speed values
        int a = 100;
        int b = 10;
        // corresponding period values
        int u = 30;
        int v = 150;

        double m = (v - u) / (double) (b - a);
        double n = (u * b - a * v) / ((double) (b - a));
        int period = (int) (m * speed + n);
        gameGrid.setSimulationPeriod(period);
//    System.out.println("new period: " + period);
    }

    private void showbtName() {
        this.getBackground().clear();                       
            
            for (int i = 0; i < GameGridManager.robotList.size(); i++) {
                NxtRobot robotnxt = GameGridManager.robotList.get(i);
                if(robotnxt.getNxt()!=null && robotnxt.getNxt().getLocation()!=null){
                    Point pt = gg.toPoint(robotnxt.getNxt().getLocation());            
                    pt.x +=30;
                    pt.y +=30;
                    this.getBackground().drawText(String.format("%s [ %s ]", robotnxt.getBtName(),i) , pt);
                }
                
            }
    }
}
  // ---------------------- End of class Nxt ---------------------
