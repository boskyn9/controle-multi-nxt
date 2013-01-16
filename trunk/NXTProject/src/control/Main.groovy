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
        
        NxtRobot robot = new NxtRobot('nxt1')        
        Gear gear = new Gear()
        robot.addPart(gear)
//        TouchSensor ts = new TouchSensor(SensorPort.S3)
//        robot.addPart(ts);        
        
        
        NxtRobot robot2 = new NxtRobot('nxt2')        
        Gear gear2 = new Gear()        
        robot2.addPart(gear2)
        
//        NxtRobot robot3 = new NxtRobot('3')        
//        Gear gear3 = new Gear()
//        robot3.addPart(gear3)
//        TouchSensor ts2 = new TouchSensor(SensorPort.S3)
//        robot2.addPart(ts2);
        
//        Thread.start {
//            while(true){
//                if(ts.isPressed()){
//                    speaker.say('stop!')
//                    gear.stop()
//                    Thread.sleep(10000)
//                }                
//            }            
//        }
        
        
        new ControlOfVoice(gear)
        
        
        
    }    
   
}

