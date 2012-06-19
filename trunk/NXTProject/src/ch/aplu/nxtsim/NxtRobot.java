// NxtRobot.java

/*
This software is part of the NxtSim library.
It is Open Source Free Software, so you may
- run the code for any purpose
- study how the code works and adapt it to your needs
- integrate all or parts of the code in your own programs
- redistribute copies of the code
- improve the code and release your improvements to the public
However the use of the code is entirely your responsibility.

Author: Aegidius Pluess, www.aplu.ch
 */
package ch.aplu.nxtsim;

import ch.aplu.jgamegrid.*;
import java.awt.*;
import java.util.*;
import javax.swing.JOptionPane;
import java.lang.reflect.*;
import java.io.*;

/**
 * Class that represents a simulated NXT robot brick. Parts (e.g. motors, sensors) may
 * be assembled into the robot to make it doing the desired job. Each instance
 * creates its own square playground (501 x 501 pixels). Some initial conditions may be modified by
 * calling static methods of the class NxtContext in a static block. A typical example
 * is:<br><br>
 * <code>
<font color="#0000ff">import</font><font color="#000000">&nbsp;ch.aplu.nxtsim.</font><font color="#c00000">*</font><font color="#000000">;</font><br>
<font color="#000000"></font><br>
<font color="#0000ff">public</font><font color="#000000">&nbsp;</font><font color="#0000ff">class</font><font color="#000000">&nbsp;Example</font><br>
<font color="#000000">{</font><br>
<font color="#000000">&nbsp;&nbsp;</font><font color="#0000ff">static</font><br>
<font color="#000000">&nbsp;&nbsp;</font><font color="#000000">{</font><br>
<font color="#000000">&nbsp;&nbsp;&nbsp;&nbsp;NxtContext.</font><font color="#000000">setStartPosition</font><font color="#000000">(</font><font color="#000000">100</font><font color="#000000">,&nbsp;</font><font color="#000000">100</font><font color="#000000">)</font><font color="#000000">;</font><br>
<font color="#000000">&nbsp;&nbsp;&nbsp;&nbsp;NxtContext.</font><font color="#000000">setStartDirection</font><font color="#000000">(</font><font color="#000000">45</font><font color="#000000">)</font><font color="#000000">;</font><br>
<font color="#000000">&nbsp;&nbsp;</font><font color="#000000">}</font><br>
<font color="#000000"></font><br>
<font color="#000000">&nbsp;&nbsp;</font><font color="#0000ff">public</font><font color="#000000">&nbsp;</font><font color="#000000">Example</font><font color="#000000">()</font><br>
<font color="#000000">&nbsp;&nbsp;</font><font color="#000000">{</font><br>
<font color="#000000">&nbsp;&nbsp;&nbsp;&nbsp;NxtRobot&nbsp;robot&nbsp;</font><font color="#c00000">=</font><font color="#000000">&nbsp;</font><font color="#0000ff">new</font><font color="#000000">&nbsp;</font><font color="#000000">NxtRobot</font><font color="#000000">()</font><font color="#000000">;</font><br>
<font color="#000000">&nbsp;&nbsp;&nbsp;&nbsp;Gear&nbsp;gear&nbsp;</font><font color="#c00000">=</font><font color="#000000">&nbsp;</font><font color="#0000ff">new</font><font color="#000000">&nbsp;</font><font color="#000000">Gear</font><font color="#000000">()</font><font color="#000000">;</font><br>
<font color="#000000">&nbsp;&nbsp;&nbsp;&nbsp;robot.</font><font color="#000000">addPart</font><font color="#000000">(</font><font color="#000000">gear</font><font color="#000000">)</font><font color="#000000">;</font><br>
<font color="#000000">&nbsp;&nbsp;&nbsp;&nbsp;gear.</font><font color="#000000">forward</font><font color="#000000">(</font><font color="#000000">5000</font><font color="#000000">)</font><font color="#000000">;</font><br>
<font color="#000000">&nbsp;&nbsp;&nbsp;&nbsp;robot.</font><font color="#000000">exit</font><font color="#000000">()</font><font color="#000000">;</font><br>
<font color="#000000">&nbsp;&nbsp;</font><font color="#000000">}</font><br>
<font color="#000000"></font><br>
<font color="#000000">&nbsp;&nbsp;</font><font color="#0000ff">public</font><font color="#000000">&nbsp;</font><font color="#0000ff">static</font><font color="#000000">&nbsp;</font><font color="#0000ff">void</font><font color="#000000">&nbsp;</font><font color="#000000">main</font><font color="#000000">(</font><font color="#00008b">String</font><font color="#000000">[]</font><font color="#000000">&nbsp;args</font><font color="#000000">)</font><br>
<font color="#000000">&nbsp;&nbsp;</font><font color="#000000">{</font><br>
<font color="#000000">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#0000ff">new</font><font color="#000000">&nbsp;</font><font color="#000000">Example</font><font color="#000000">()</font><font color="#000000">;</font><br>
<font color="#000000">&nbsp;&nbsp;</font><font color="#000000">}</font><br>
<font color="#000000">}</font><br>
 * </code><br><br>
 * In principle you may remove the static header and use the program unmodified
 * for the real NXT robot using the NxtJLib or NxtJLibA library (see www.aplu.ch/nxt).<br><br>
 *
 * Because NxtRobot extends Actor all public methods of Actor are exposed. Some
 * of them are overridden and
 */
public class NxtRobot {

    //
//    private final static int nbRotatableSprites = 360;
    private static GameGrid gg;
    private static Nxt nxt = null;
    private String btName = null;
    private ArrayList<Part> parts;

    /**
     * Creates a robot with its playground using defaults from NxtContext.
     */
    public NxtRobot() {
        this(null);
    }

    public NxtRobot(String btName) {
        gg = GameGridManager.getInstance();

        if (btName == null) {
            this.btName = JOptionPane.showInputDialog("Enter Bluetooth Name").trim();
        } else {
            this.btName = btName;
        }

        Location location = NxtContext.startLocation;
        if (GameGridManager.robotList.size() > 0) {
            NxtRobot lastNxt = GameGridManager.robotList.last();
            if (lastNxt != null) {
                location = lastNxt.getNxt().getLocation();
                location.x += 50;
            }
        }

        if (GameGridManager.robotList.add(this)) {
            parts = new ArrayList<Part>();
            nxt = new Nxt(location, NxtContext.startDirection, gg, this);
        }

    }

    /**
     * Assembles the given part into the robot.
     * If already connected, initialize the part.
     * @param part the part to assemble
     */
    public void addPart(Part part) {
        synchronized (NxtRobot.class) {
            if (parts != null) {
                parts.add(part);
                gg.addActor(part, nxt.getPartLocation(part), nxt.getDirection());
                gg.setPaintOrder(getClass(), part.getClass());  // On top of obstacles
                gg.setActOrder(getClass());  // First
            }
        }
    }

    /**
     * Returns the instance reference of the GameGrid.
     * @return the reference of the GameGrid
     */
    public static GameGrid getGameGrid() {
        return gg;
    }

    /**
     * Returns the instance reference of the Nxt actor.
     * @return the reference of the Nxt
     */
    public static Actor getNxt() {
        return nxt;
    }

    /**
     * Stops any motion and performs a cleanup of all parts.
     */
    public void exit() {
        synchronized (NxtRobot.class) {
            for (Part p : parts) {
                p.cleanup();
            }
        }
        gg.doPause();
    }

    public ArrayList<Part> getParts() {
        return this.parts;
    }

    public String getBtName() {
        return btName;
    }

    /**
     * Returns the current library version.
     * @return a string telling the current version
     */
    public static String getVersion() {
        return SharedConstants.VERSION;
    }

    protected static void fail(String message) {
        JOptionPane.showMessageDialog(null, message, "Fatal Error", JOptionPane.ERROR_MESSAGE);
        System.exit(0);
    }

    /**
     * Resets Nxt to start location/direction.
     */
    public void reset() {
        Actor nxt = getNxt();
        nxt.reset();
        nxt.setLocation(nxt.getLocationStart());
        nxt.setDirection(nxt.getDirectionStart());
    }
}
