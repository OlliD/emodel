/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.agai.emodel.emotionstrategyselector;

import com.sun.jmx.snmp.Timestamp;
import de.unibi.agai.emodel.emotionstrategyselector.gui.StrategySelectorGui;
import de.unibi.agai.emodel.emotionstrategyselector.robotconnector.HCGui;
import de.unibi.agai.emodel.emotionstrategyselector.robotconnector.HeadPositions;
import de.unibi.agai.emodel.emotionstrategyselector.robotconnector.Robot;
import de.unibi.agai.emodel.emotionstrategyselector.xcf.MemoryConnector;
import de.unibi.agai.robots.Actuator;
import de.unibi.agai.robots.Actuator;
import de.unibi.flobi.Actuators;
import static de.unibi.flobi.Actuators.neck_pan;
import static de.unibi.flobi.Actuators.neck_roll;
import static de.unibi.flobi.Actuators.neck_tilt;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.xcf.InitializeException;
import net.sf.xcf.memory.MemoryException;
import net.sf.xcf.naming.NameNotFoundException;

/**
 *
 * @author odamm
 */
public class Controller {

    private Robot r;
    private HeadPositions hp;
    MemoryConnector mc;
    private String strategicEmotion;
    private String mimircyEmotion;
    private String schematicEmotion;
    private StrategySelectorGui ssg;
    private int layer1Time;
    private int layer2Time;
    private int layer3Time;
    private boolean layer1Active;
    private boolean layer2Active;
    private boolean layer3Active;
    private boolean run = false;
    private Timestamp tstamp;
    private String currentStrategy;
    private Map<Actuators, Float> userOrientation;

    public enum strategy {

        ONOFF,
        TIME,
        PERCENTAGE
    }

    public Controller() throws MemoryException, InitializeException, NameNotFoundException, IOException, ExecutionException, InterruptedException, TimeoutException {
        userOrientation = new HashMap<Actuators, Float>();

        ssg = new StrategySelectorGui();
        ssg.setVisible(true);
        addListener();

        System.err.println("StrategySelector startet!");
        mc = new MemoryConnector(ssg);
        r = new Robot();
        hp = new HeadPositions();

        int min_pan = (int) (r.getMin(Actuators.neck_pan));
        int max_pan = (int) (r.getMax(Actuators.neck_pan));
        int min_tilt = (int) (r.getMin(Actuators.neck_tilt));
        int max_tilt = (int) (r.getMax(Actuators.neck_tilt));
        int min_roll = (int) (r.getMin(Actuators.neck_roll));
        int max_roll = (int) (r.getMax(Actuators.neck_roll));

        List<String> poses = new ArrayList();
        for (String s : hp.getPositions().keySet()) {
            System.out.println("Position added");

            poses.add(s);
        }
        Collections.sort(poses);

        // Connection to the Robot
        HCGui eg = new HCGui(r, hp);
        eg.setVisible(true);
        mc.startListening();
    }

    // Worker-Loop, die zu zeigende Emotion wir abgefragt und an den Roboter gesendet. Der Cooldown von 5sek 
    // führt zu einem neutralen Gesichtsausdruck
    public void worker() throws InterruptedException, IOException, ExecutionException, TimeoutException {
        new Thread() {
            @Override
            public void run() {
                String emotion = "";
                int cooldown = 0;
                strategy st = strategy.valueOf(currentStrategy.toUpperCase());
                switch (st) {
                    case TIME:
                        while (run) {
                            try {
                                System.err.println("Running TIME");
                                emotion = mc.expressEmotion();
                                if (emotion != "") {
                                    try {
                                        sendEmotion(emotion);
                                    } catch (IOException ex) {
                                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                                    } catch (ExecutionException ex) {
                                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                                    } catch (TimeoutException ex) {
                                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                                    } catch (InterruptedException ex) {
                                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                                Thread.sleep(2000);
                                cooldown++;

                                if (cooldown == 5) {
                                    cooldown = 0;
                                    try {
                                        r.executeMovement(hp.getPosition("neutral").getActuatorList(), 30, 150);
                                    } catch (IOException ex) {
                                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                                    } catch (ExecutionException ex) {
                                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                                    } catch (InterruptedException ex) {
                                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                                    } catch (TimeoutException ex) {
                                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            } catch (InterruptedException ex) {
                                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }
                        break;
                    case ONOFF:
                        while (run) {
                            try {
                                Thread.sleep(1000);
                                System.err.println("Running OnOff");
                                cooldown++;

                                if (layer1Active) {
                                    emotion = mc.expressEmotion();

                                    if (emotion != "") {
                                        try {
                                            System.out.println("MIMICRY");

                                            sendEmotion(emotion);
                                        } catch (IOException ex) {
                                            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                                        } catch (ExecutionException ex) {
                                            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                                        } catch (TimeoutException ex) {
                                            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                                        } catch (InterruptedException ex) {
                                            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }
                                }
                                if (layer2Active) {
                                    // PUT HERE SCHEMATIC FUNCTION
                                    schematic();
                                }

                                if (layer3Active) {
                                    // PUT HERE STRATEGEC FUNCTION
                                }
                                if (cooldown == 3) {
                                    System.out.println("Look back");
                                    cooldown = 0;
                                    try {
                                        r.executeMovement(hp.getPosition("neutral").getActuatorList(), 30, 150);
                                    } catch (IOException ex) {
                                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                                    } catch (ExecutionException ex) {
                                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                                    } catch (InterruptedException ex) {
                                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                                    } catch (TimeoutException ex) {
                                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            } catch (InterruptedException ex) {
                                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IOException ex) {
                                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (ExecutionException ex) {
                                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (TimeoutException ex) {
                                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }
                        break;
                }

            }
        }.start();

    }

    private void sendEmotion(String emotion) throws IOException, ExecutionException, TimeoutException, InterruptedException {
        r.executeMovement(hp.getPosition(emotion.toLowerCase()).getActuatorList(), 30, 150);

    }

    private void schematic() throws IOException, ExecutionException, InterruptedException, TimeoutException {
        String[] pos = mc.lookAtPosition();
        System.out.println("Get UserPos " + pos[0] + " " + pos[1] + " " + pos[2]);
        if (pos[0] != null && pos[1] != null && pos[2] != null) {
            lookAtPos(Integer.parseInt(pos[0]), Integer.parseInt(pos[1]), Integer.parseInt(pos[2]));

        }
    }

    private void onOffStrategy() throws IOException, ExecutionException, TimeoutException, InterruptedException {

        System.out.println("OnOffStrategic selected");
        currentStrategy = "onoff";

        Actuator act;

        String emotion = "";
        int cooldown = 0;
        while (true) {
            emotion = mc.expressEmotion();
            if (emotion != "") {
                sendEmotion(emotion);
            }
            cooldown++;
            Thread.sleep(1000);
            if (cooldown == 5) {
                cooldown = 0;
                r.executeMovement(hp.getPosition("neutral").getActuatorList(), 30, 150);
            }

        }

    }

    private void timeStrategy() throws IOException, ExecutionException, TimeoutException, InterruptedException {
        if (layer1Time > 0 || layer2Time > 0 || layer3Time > 0) {
            currentStrategy = "time";
            long currentTime = tstamp.getDate().getTime();
            System.out.println("Time Strategy selected - current Time" + tstamp);
            String emotion = "";
            int cooldown = 0;
            while (true) {
                emotion = mc.expressEmotion();
                if (emotion != "") {
                    sendEmotion(emotion);
                }
                cooldown++;
                Thread.sleep(1000);
                if (cooldown == 5) {
                    cooldown = 0;
                    r.executeMovement(hp.getPosition("neutral").getActuatorList(), 30, 150);
                }

            }
        }
    }

    private void lookAtPos(int x, int y, int z) throws IOException, ExecutionException, InterruptedException, TimeoutException {
        userOrientation.put(Actuators.neck_tilt, 0f);
        userOrientation.put(Actuators.neck_roll, -2f);

        float a = x;
        float b = y;
        float c = z;
        float hyp = (float) Math.sqrt((a * a) + (b * b));

        float beta = (float) Math.toDegrees(Math.acos(((Math.pow(a, 2) + Math.pow(hyp, 2) - Math.pow(b, 2))) / (2 * a * hyp)));
        System.out.println("a " + a + " b " + b + " hyp " + hyp + " beta " + beta);

        float hyp_gamma = (float) Math.sqrt((a * a) + (c * c));

        float gamma = (float) Math.toDegrees(Math.acos(((Math.pow(a, 2) + Math.pow(hyp_gamma, 2) - Math.pow(c, 2))) / (2 * a * hyp_gamma)));

        if (b > 0) {
            beta = beta * -1;
        }

        userOrientation.put(Actuators.neck_pan, beta);
        userOrientation.put(Actuators.neck_tilt, gamma);
        r.executeMovement(userOrientation, 20, 20);
    }

    // ACTIONLISTENER 
    private void addListener() {
        this.ssg.setLayer1CheckboxListener(new Layer1CheckboxListener());
        this.ssg.setLayer2CheckboxListener(new Layer2CheckboxListener());
        this.ssg.setLayer3CheckboxListener(new Layer3CheckboxListener());
        this.ssg.setRadioButtonOnOffStragtegyListener(new OnOffStragtegyActionListener());
        this.ssg.setRadioButtonTimeStrategyListener(new TimeStragtegyActionListener());
        this.ssg.setjButton2Listener(new TimeStrategySetActionListener());
        this.ssg.setjButton3Listener(new OnOffStrategySetActionListener());
        this.ssg.setStopButtonListener(new StopButtonListener());
        this.ssg.setStartButtonListener(new StartButtonListener());

    }

    class Layer1CheckboxListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            boolean wert = ssg.getOnOffCheckBoxLayer1();
            System.out.println("Set Layer 1? " + ssg.getOnOffCheckBoxLayer1());
            try {
                mc.startListeningLayer1(wert);
            } catch (MemoryException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    class Layer2CheckboxListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            boolean wert = ssg.getOnOffCheckBoxLayer2();
            System.out.println("Set Layer 2? " + ssg.getOnOffCheckBoxLayer2());
            try {
                mc.startListeningLayer2(wert);
            } catch (MemoryException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    class Layer3CheckboxListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            boolean wert = ssg.getOnOffCheckBoxLayer3();
            System.out.println("Set Layer 3? " + ssg.getOnOffCheckBoxLayer3());
            try {
                mc.startListeningLayer3(wert);
            } catch (MemoryException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    class StopButtonListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            System.out.println("Stopp requested");
            run = false;
        }

    }

    class StartButtonListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            System.out.println("Start requested");
            run = true;
            try {
                worker();
            } catch (InterruptedException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            } catch (TimeoutException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    class TimeStragtegyActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            System.out.println("TimeTime");
            ssg.toggleTimeStrategy();
        }
    }

    class OnOffStragtegyActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            System.out.println("OnOffOnOff");
            ssg.toggleOnOffStrategy();
        }
    }

    class TimeStrategySetActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            layer1Time = ssg.getLayer1Time();
            layer2Time = ssg.getLayer2Time();
            layer3Time = ssg.getLayer3Time();
            System.out.println("TimeTimeTimeSET to " + layer1Time + " " + layer2Time + " " + layer3Time);
            currentStrategy = "Time";
        }
    }

    class OnOffStrategySetActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            layer1Active = ssg.getOnOffCheckBoxLayer1();
            layer2Active = ssg.getOnOffCheckBoxLayer2();
            layer3Active = ssg.getOnOffCheckBoxLayer3();
            System.out.println("OnOffOnOffSET to " + layer1Active + " " + layer2Active + " " + layer3Active);
            currentStrategy = "OnOff";
        }
    }

}
