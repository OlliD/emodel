package de.unibi.agai.emodel.emotionstrategyselector.robotconnector;


import de.unibi.agai.event.MeasurementEvent;
import de.unibi.agai.robots.Actuator;
import de.unibi.agai.robots.ActuatorValue;
import de.unibi.agai.robots.ActuatorVariable;
import de.unibi.agai.robots.Unit;
import de.unibi.flobi.*;
import de.unibi.flobi.xs2.XS2FlobiControlBase;
import de.unibi.robots.actuator.RangeModelActuator;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author akipp
 */
public class Robot {

    private static final Logger LOGGER = Logger.getLogger(Robot.class.getName());
    private static final boolean SAVE_STATE = false;
    private static XS2FlobiControlBase flobiControl;
    public Map<Actuators, RangeModelActuator> actuators;

    public Robot() {
        try {
            flobiControl = new XS2FlobiControlBase(true);

            if (connect()) {
            } else {
                LOGGER.log(Level.SEVERE, "Error connecting to robot.");
            }

        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        LOGGER.log(Level.INFO, "Robot initilized");
    }

    public float getMax(Actuators a) throws InterruptedException, IOException, NullPointerException {
        List<MeasurementEvent> measure = flobiControl.getVirtualActuators().get(a).measure(ActuatorVariable.CalibrationTickPosMax);
        float max = measure.get(0).measurements[1].getValue();
        System.out.println(a.name() + " max: " +max);
        return max;
    }

        public float getMin(Actuators a) throws InterruptedException, IOException, NullPointerException {
            
        List<MeasurementEvent> measure = flobiControl.getVirtualActuators().get(a).measure(ActuatorVariable.CalibrationTickPosMin);
        float min = measure.get(0).measurements[1].getValue();
        System.out.println(a.name() + " min: " +min);
        return min;
    }
    
    public final List<String> executeMovement(Map<Actuators, Float> returnMap, int ramp, int maxSpeed) throws IOException, ExecutionException, InterruptedException, TimeoutException {
        LOGGER.log(Level.FINE, "Execute motion with ramp {0} and maxSpeed {1}", new Object[]{ramp, maxSpeed});
        MotionTaskHandler motionTaskHandler = flobiControl.getMotionTaskHandler();

        List conditions = new ArrayList<Condition>(returnMap.size());

        MotionChunk[] m = new MotionChunk[returnMap.keySet().size()];

        List<String> messages = new ArrayList<String>();

        int index = 0;
        for (Actuators a : returnMap.keySet()) {
            /*
             RangeModelActuator rma = actuators.get(a);
             if(!rma.isWithinLimits(returnMap.get(a))) {
             System.out.println(a.name() + ": Value not in Range!");
             continue;
             } */
            LOGGER.log(Level.FINE, "Setting actuator {0} with value {1}", new Object[]{a.name(), returnMap.get(a)});
            m[index++] = new MotionChunk(a, EnumSet.noneOf(MotionChunk.Flags.class),
                    new ArrayList<Condition>(0),
                    new ActuatorValue(ActuatorVariable.Active, 255, Unit.on_off),
                    new ActuatorValue(ActuatorVariable.PIDSpeedRamp, ramp),
                    new ActuatorValue(ActuatorVariable.PIDMaxSpeed, maxSpeed),
                    new ActuatorValue(ActuatorVariable.TargetPosition,
                    returnMap.get(a), Unit.angle_degrees));
            conditions.add(new RobustPositionCondition(a, returnMap.get(a), Unit.angle_degrees, 0.5, 100));
        }
        if (conditions.size() > 0) {
            m[m.length - 1].conditions.addAll(conditions);

            LOGGER.log(Level.FINEST, "Execute {0}", Arrays.asList(m));
            //motionTaskHandler.submit(m).get();
            motionTaskHandler.submit(false, Arrays.asList(m)).get(3, TimeUnit.SECONDS);
            LOGGER.log(Level.INFO, "Motion complete.");
        } else {
            System.out.println("Conditions empty, not executing movement.");
        }
        return null;
    }

//    public final void executeSinglePosition(Map<Actuators, Float> returnMap, int ramp, int maxSpeed) throws IOException, ExecutionException, InterruptedException, TimeoutException {
//        LOGGER.log(Level.FINE, "Execute motion with ramp {0} and maxSpeed {1}", new Object[]{ramp, maxSpeed});
//        MotionTaskHandler motionTaskHandler = flobiControl.getMotionTaskHandler();
//
//        List conditions = new ArrayList<Condition>(returnMap.size());
//
//        MotionChunk[] m = new MotionChunk[returnMap.keySet().size()];
//
//        int index = 0;
//        for (Actuators a : returnMap.keySet()) {
//            LOGGER.log(Level.FINE, "Setting actuator {0} with value {1}", new Object[]{a.name(), returnMap.get(a)});
//            m[index++] = new MotionChunk(a, EnumSet.noneOf(MotionChunk.Flags.class),
//                    new ArrayList<Condition>(0),
//                    new ActuatorValue(ActuatorVariable.Active, 255, Unit.on_off),
//                    new ActuatorValue(ActuatorVariable.PIDSpeedRamp, ramp),
//                    new ActuatorValue(ActuatorVariable.PIDMaxSpeed, maxSpeed),
//                    
//                    new ActuatorValue(ActuatorVariable.TargetPosition,
//                    returnMap.get(a), Unit.angle_degrees));
//            conditions.add(new RobustPositionCondition(a, returnMap.get(a), Unit.angle_degrees, 0.5, 100));
//        }
//        m[m.length - 1].conditions.addAll(conditions);
//
//        LOGGER.log(Level.FINEST, "Execute {0}", Arrays.asList(m));
//        //motionTaskHandler.submit(m).get();
//        motionTaskHandler.submit(false, Arrays.asList(m)).get(3, TimeUnit.SECONDS);
//        LOGGER.log(Level.INFO, "Motion complete.\n\n");
//    }    
    public final boolean connect() throws InterruptedException {

        try {
            actuators = flobiControl.getVirtualActuators();

            for (Actuators a : actuators.keySet()) {
                RangeModelActuator rma = actuators.get(a);

                System.out.println(a.name() + ": min/max " + rma.getMinAngle() + "/" + rma.getMaxAngle() + " TYPE: ");
            }
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING,
                    "Comunication with robot failed: {0}", ex.getMessage());
            return false;
        }
        LOGGER.log(Level.FINE, "Current actuators: {0}", actuators);
        return true;
    }
}
