/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.aplu.nxtsim;

import ch.aplu.jgamegrid.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author boskyn9
 */
public class GameGridManager {

    private static GameGrid gg;
    private final static int nbRotatableSprites = 360;
    public static NxtRobotList robotList;

    public static GameGrid getInstance() {
        if (gg == null) {
            robotList = new NxtRobotList();
            
            // definir obstáculos
            NxtContext.useObstacle("sprites/channel.gif", 400, 300);
            
            
            gg = new GameGrid(800, 600, 1, null,
                    NxtContext.imageName, NxtContext.isNavigationBar, nbRotatableSprites);

            gg.setSimulationPeriod(SharedConstants.simulationPeriod);
            if (NxtContext.xLoc > 0 && NxtContext.yLoc > 0) {
                gg.setLocation(NxtContext.xLoc, NxtContext.yLoc);
            }
            gg.setBgColor(Color.white);
            gg.setTitle("");

            int nbObstacles = NxtContext.obstacles.size();
            for (int i = 0; i < nbObstacles; i++) {
                gg.addActor(NxtContext.obstacles.get(i),
                        NxtContext.obstacleLocations.get(i));
            }
        }
        

        return gg;
    }

}
