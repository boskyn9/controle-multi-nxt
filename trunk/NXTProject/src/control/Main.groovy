/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package control

import ch.aplu.nxtsim.*
import synthesizer.Speaker
/**
 *
 * @author boskyn9
 */
class Main {
    
    static void main(def args){
        Speaker speaker = Speaker.getInstance()
        NxtRobot robot = new NxtRobot()
        Gear gear = new Gear()
        robot.addPart(gear)
        TouchSensor ts = new TouchSensor(SensorPort.S3);
        robot.addPart(ts);        
        
        Thread.start {
            while(true){
                if(ts.isPressed()){
                    speaker.say('stop!')
                    gear.stop()                    
                }                
            }            
        }
        
        new ControlOfVoice(robot,gear)
        
        
        
    }    
        
    static {     
        //cima
        NxtContext.useObstacle("sprites/bar0.gif", 200, 10);
        NxtContext.useObstacle("sprites/bar0.gif", 499, 10);
        // direita
        NxtContext.useObstacle("sprites/bar1.gif", 689, 220);
        NxtContext.useObstacle("sprites/bar1.gif", 689, 500);
        //baixo
        NxtContext.useObstacle("sprites/bar2.gif", 200, 689);
        NxtContext.useObstacle("sprites/bar2.gif", 499, 689);
        //esquerdo
        NxtContext.useObstacle("sprites/bar3.gif", 10, 200);
        NxtContext.useObstacle("sprites/bar3.gif", 10, 499);
    }
	
}

