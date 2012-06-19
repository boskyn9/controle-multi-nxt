/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.aplu.nxtsim;

import java.util.ArrayList;
import java.util.AbstractList;
import java.util.List;



/**
 *
 * @author boskyn9
 */
public class NxtRobotList extends AbstractList<NxtRobot> {

    private List<NxtRobot> nxtList;

    public NxtRobotList() {
        nxtList = new ArrayList<NxtRobot>();
    }    
    
    @Override
    public NxtRobot get(int index) {
        return nxtList.get(index);
    }

    @Override
    public int size() {
        return nxtList.size();
    }

    public boolean contains(NxtRobot nxt) {
        String btName = nxt.getBtName();
        for (NxtRobot nxtRobot : nxtList) {
            if(nxtRobot.getBtName().equals(btName)){
                return true;                
            }
        }
        return false;
    }
    
    public NxtRobot last(){
        return nxtList.get(nxtList.size()-1);
    }
    
    @Override
    public boolean add(NxtRobot nxt){
        if(!contains(nxt)){
            return nxtList.add(nxt);
        }
        return false;
    }
        
}
