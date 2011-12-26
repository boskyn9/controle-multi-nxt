package examples

/**
 * Created by IntelliJ IDEA.
 * User: boskyn9
 * Date: 30/11/11
 * Time: 22:49
 * To change this template use File | Settings | File Templates.
 */
// TurtleEventEx1.java

import ch.aplu.nxtsim.*;
import ch.aplu.util.*;

class TurtleEventEx1
{
  TurtleEventEx1()
  {
    TurtleRobot robot = new TurtleRobot();
    Console.println("Up/Down cursor key: forward/backward");
    Console.println("Left/Right cursor key: turn left/right");
    Console.println("Title bar x button to terminate");

    while (true)
    {
      int kCode = Console.getKeyCodeWait();
      println kCode
      switch (kCode)
      {
        case 38: // Up
          robot.forward(50);
          break;
        case 40: // Down
          robot.backward(50);
          break;
        case 37: // Left
          robot.left(45);
          break;
        case 39: // Right
          robot.right(45);
          break;
      }
    }
  }

  public static void main(String[] args)
  {
    new TurtleEventEx1();
  }
}