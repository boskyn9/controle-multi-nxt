// Gear.java

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
import ch.aplu.util.*;

/**
 * Combines two motors on an axis to perform a car-like movement.
 */
public class Gear extends Part
{
  protected enum GearState
  {
    FORWARD, BACKWARD, LEFT, RIGHT, STOPPED
  };
  private static final Location pos = new Location(0, 0);
  private final int DEFAULT_SPEED = 50;
  private GearState state = GearState.STOPPED;
  private double radius;
  private int speed = DEFAULT_SPEED;
  private boolean isMoving = false;

  /**
   * Creates a gear instance with right motor plugged into port A, left motor plugged into port B.
   */
  public Gear()
  {
    super("sprites/gear.gif", pos);
  }

  protected void cleanup()
  {
    state = GearState.STOPPED;
    isMoving = false;
  }

  /**
   * Starts the forward movement.
   * Method returns immediately, while the movement continues.
   * @return the object reference to allow method chaining
   */
  public Gear forward()
  {
    state = GearState.FORWARD;
    if (speed != 0)
      isMoving = true;
    return this;
  }

  /**
   * Starts the forward movement for the given duration (in ms) and stops.
   * Method returns at the end of the given duration,
   * @param duration the duration (in ms)
   * @return the object reference to allow method chaining
   */
  public Gear forward(int duration)
  {
    HiResAlarmTimer t = new HiResAlarmTimer(duration * 1000, true);
    state = GearState.FORWARD;
    while (t.isRunning())
    {
    }
    state = GearState.STOPPED;
    return this;
  }

  /**
   * Starts the backward movement.
   * Method returns immediately, while the movement continues.
   * @return the object reference to allow method chaining
   */
  public Gear backward()
  {
    state = GearState.BACKWARD;
    if (speed != 0)
      isMoving = true;
    return this;
  }

  /**
   * Starts the backward movement for the given duration (in ms) and stops.
   * Method returns at the end of the given duration,
   * @param duration the duration (in ms)
   * @return the object reference to allow method chaining
   */
  public Gear backward(int duration)
  {
    HiResAlarmTimer t = new HiResAlarmTimer(duration * 1000, true);
    state = GearState.BACKWARD;
    while (t.isRunning())
    {
    }
    state = GearState.STOPPED;
    return this;
  }

  /**
   * Stops the movement.
   * @return the object reference to allow method chaining.
   */
  public Gear stop()
  {
    state = GearState.STOPPED;
    isMoving = false;
    return this;
  }

  /**
   * Starts to rotate left (center of rotation at middle of the wheel axes).
   * Method returns immediately, while the movement continues.
   * @return the object reference to allow method chaining
   */
  public Gear left()
  {
    state = GearState.LEFT;
    radius = 0;
    if (speed != 0)
      isMoving = true;
    return this;
  }

  /**
   * Starts to rotate left (center of rotation at middle of the wheel axes)
   * for the given duration (in ms) and stops.
   * Method returns at the end of the given duration,
   * @param duration the duration (in ms)
   * @return the object reference to allow method chaining
   */
  public Gear left(int duration)
  {
    HiResAlarmTimer t = new HiResAlarmTimer(duration * 1000, true);
    left();
    while (t.isRunning())
    {
    }
    state = GearState.STOPPED;
    return this;
  }

  /**
   * Starts to rotate right (center of rotation at middle of the wheel axes).
   * Method returns immediately, while the movement continues.
   * @return the object reference to allow method chaining
   */
  public Gear right()
  {
    state = GearState.RIGHT;
    radius = 0;
    if (speed != 0)
      isMoving = true;
    return this;
  }

  /**
   * Starts to rotate right (center of rotation at middle of the wheel axes)
   * for the given duration (in ms) and stops.
   * Method returns at the end of the given duration,
   * @param duration the duration (in ms)
   * @return the object reference to allow method chaining
   */
  public Gear right(int duration)
  {
    HiResAlarmTimer t = new HiResAlarmTimer(duration * 1000, true);
    right();
    while (t.isRunning())
    {
    }
    state = GearState.STOPPED;
    return this;
  }

  /**
   * Starts to move to the left on an arc with given radius.
   * Method returns immediately, while the movement continues.
   * @param radius the radius of the arc; if negative, moves backward
   * @return the object reference to allow method chaining
   */
  public Gear leftArc(double radius)
  {
    state = GearState.LEFT;
    this.radius = SharedConstants.pixelPerMeter * radius;
    if (speed != 0)
      isMoving = true;
    return this;
  }

  /**
   * Starts to move left on an arc with given radius
   * for the given duration (in ms) and stops.
   * Method returns at the end of the given duration,
   * @param radius the radius of the arc; if negative, moves backward
   * @param duration the duration (in ms)
   * @return the object reference to allow method chaining
   */
  public Gear leftArc(double radius, int duration)
  {
    HiResAlarmTimer t = new HiResAlarmTimer(duration * 1000, true);
    leftArc(radius);
    while (t.isRunning())
    {
    }
    state = GearState.STOPPED;
    return this;
  }

  /**
   * Starts to move to the right on an arc with given radius.
   * Method returns immediately, while the movement continues.
   * @param radius the radius of the arc; if negative, moves backward
   * @return the object reference to allow method chaining
   */
  public Gear rightArc(double radius)
  {
    state = GearState.RIGHT;
    this.radius = SharedConstants.pixelPerMeter * radius;
    if (speed != 0)
      isMoving = true;
    return this;
  }

  /**
   * Starts to move right on an arc with given radius
   * for the given duration (in ms) and stops.
   * Method returns at the end of the given duration,
   * @param radius the radius of the arc; if negative, moves backward
   * @param duration the duration (in ms)
   * @return the object reference to allow method chaining
   */
  public Gear rightArc(double radius, int duration)
  {
    HiResAlarmTimer t = new HiResAlarmTimer(duration * 1000, true);
    rightArc(radius);
    while (t.isRunning())
    {
    }
    state = GearState.STOPPED;
    return this;
  }

  protected GearState getState()
  {
    return state;
  }

  protected double getRadius()
  {
    return radius;
  }

  /**
   * Sets the speed to the given value (arbitrary units).
   * @param speed 0..100
   * @return the object reference to allow method chaining
   */
  public Gear setSpeed(int speed)
  {
    this.speed = speed;
    if (speed != 0 && state != GearState.STOPPED)
      isMoving = true;
    return this;
  }

  /**
   * Returns the current speed (arbitrary units).
   * @return speed 0..100
   */
  public int getSpeed()
  {
    return speed;
  }

  /**
   * Checks if one or both motors are rotating
   * @return true, if gear is moving; otherwise false
   */
  public boolean isMoving()
  {
    return isMoving;
  }
}
