package de.unibi.agai.emodel.emotionstrategyselector.robotconnector;


import de.unibi.flobi.Actuators;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author akipp
 */


public class PositionElement {
    private Map<Actuators,Float> actuatorList = new EnumMap<Actuators,Float>(Actuators.class);
    private Map<SpeedNames, List<Integer>> speednames = new EnumMap<SpeedNames,List<Integer>>(SpeedNames.class);
    
    private int ramp;
    private int maxSpeed;
    private String posName;
    
    
    public String getPosName() {
        return posName;
    }
    
    public PositionElement(Map<Actuators,Float> l, int r, int s, String p, Map<SpeedNames, List<Integer>> speeds){
        ramp = r;
        maxSpeed = s;
        actuatorList = l;
        posName = p;
        speednames = speeds;
    }
    
    public void printValues() {
        System.out.println("\tPosE: ramp: " + ramp);
        System.out.println("\tPosE: speed: " + maxSpeed);
        for(Actuators a : actuatorList.keySet()) {
            System.out.println("\tPosE: actuator " + a.toString() + " " + actuatorList.get(a));
        }        
    }
    
    public int getMaxSpeed() {
        return maxSpeed;
    }
    
    public int getRamp() {
        return ramp;
    }
    
    public Map<Actuators,Float> getActuatorList() {
        return actuatorList;
    }
}
