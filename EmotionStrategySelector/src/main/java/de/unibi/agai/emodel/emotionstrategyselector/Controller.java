/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.agai.emodel.emotionstrategyselector;

import de.unibi.agai.dapi.pack.PackerNotFoundException;
import de.unibi.agai.eb.BusException;
import de.unibi.agai.emodel.emotionstrategyselector.gui.StrategySelectorGui;
import de.unibi.agai.emodel.emotionstrategyselector.robotconnector.HCGui;
import de.unibi.agai.emodel.emotionstrategyselector.robotconnector.HeadPositions;
import de.unibi.agai.emodel.emotionstrategyselector.robotconnector.Robot;
import de.unibi.agai.emodel.emotionstrategyselector.xcf.MemoryConnector;
import de.unibi.agai.emotionlib.communication.EmotionServer;
import de.unibi.agai.emotionlib.communication.EmotionTaskHandler;
import de.unibi.agai.emotionlib.output.EmotionalExpr;
import de.unibi.flobi.Actuators;
import java.awt.Color;
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
    private MemoryConnector mc;
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
    private String currentStrategy;
    private Map<Actuators, Float> userOrientation;
    private String emotion = "";
    private long startTime;
    private long runtime;
    private EmotionTaskHandler eth;
    private EmotionServer es;
    private HCGui eg;
    private float emoImpuls = 0f;

    public enum strategy {

        ONOFF,
        TIME,
        PERCENTAGE
    }

    public Controller() throws MemoryException, InitializeException, NameNotFoundException, IOException, ExecutionException, InterruptedException, TimeoutException, BusException, PackerNotFoundException {
        //cooldown = 0;
        userOrientation = new HashMap<Actuators, Float>();

        ssg = new StrategySelectorGui();
        ssg.setVisible(true);
        addListener();

        r = new Robot();
        hp = new HeadPositions();

        eth = new EmotionTaskHandler();
        eth.activateVisualizion();

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
        mc = new MemoryConnector();
        mc.startListening();
    }

    // Worker-Loop, die zu zeigende Emotion wir abgefragt und an den Roboter gesendet. Der Cooldown von 5sek 
    // führt zu einem neutralen Gesichtsausdruck
    public void worker() throws InterruptedException, IOException, ExecutionException, TimeoutException {
        new Thread() {
            @Override
            public void run() {
                int layer1_cooldown = 0;
                int layer2_cooldown = 0;
                int layer3_cooldown = 0;
                switch (strategy.valueOf(currentStrategy.toUpperCase())) {
                    case TIME:
                        boolean layer1Running = true;
                        boolean layer2Running = true;
                        boolean layer3Running = true;

                        while (run) {
                            try {
                                System.err.println("Running TIME " + (System.currentTimeMillis() / 10000));
                                runtime = (System.currentTimeMillis() - startTime) / 1000;
                                if (runtime < layer1Time) {
                                    if (layer1_cooldown == 0) {
                                        mimicry();
                                        layer1_cooldown = 6;
                                    } else if (layer1_cooldown != 0) {
                                        layer1_cooldown--;
                                    }
                                } else {
                                    layer1Running = false;
                                }

                                if (runtime < layer2Time) {
                                    if (layer2_cooldown == 0) {
                                        schematic();
                                        layer2_cooldown = 6;
                                    } else if (layer2_cooldown != 0) {
                                        layer2_cooldown--;
                                    }
                                } else {
                                    layer2Running = false;
                                }

                                if (runtime < layer3Time) {
                                    if (layer3_cooldown == 0) {
                                        strategic();
                                        layer3_cooldown = 6;
                                    } else if (layer3_cooldown != 0) {
                                        layer3_cooldown--;
                                    }
                                } else {
                                    layer3Running = false;
                                }

                                if (!layer1Running && !layer2Running && !layer3Running) {
                                    run = false;
                                    ssg.setLayer1Text("STOPPED");
                                    ssg.setLayer2Text("STOPPED");
                                    ssg.setLayer3Text("STOPPED");

                                }

                                Thread.sleep(500);

                            } catch (InterruptedException ex) {
                                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IOException ex) {
                                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (ExecutionException ex) {
                                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (TimeoutException ex) {
                                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (MemoryException ex) {
                                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }
                        break;
                    case ONOFF:
                        System.err.println("Running OnOff");
                        while (run) {
                            try {
                                //cooldown++;

                                if (layer1Active) {
                                    if (layer1_cooldown == 0) {
                                        mimicry();
                                        layer1_cooldown = 6;
                                    } else if (layer1_cooldown != 0) {
                                        layer1_cooldown--;
                                    }
                                }
                                if (layer2Active) {
                                    // PUT HERE SCHEMATIC FUNCTION
                                    if (layer1_cooldown == 0) {
                                        schematic();
                                        layer1_cooldown = 6;
                                    } else if (layer1_cooldown != 0) {
                                        layer1_cooldown--;
                                    }
                                }

                                if (layer3Active) {
                                    if (layer1_cooldown == 0) {
                                        strategic();
                                        layer1_cooldown = 6;
                                    } else if (layer1_cooldown != 0) {
                                        layer1_cooldown--;
                                    }

                                    // PUT HERE STRATEGEC FUNCTION
                                }
                                Thread.sleep(200);

                            } catch (InterruptedException ex) {
                                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IOException ex) {
                                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (ExecutionException ex) {
                                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (TimeoutException ex) {
                                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (MemoryException ex) {
                                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }
                        break;
                }
            }
        }.start();

    }

    private void schematic() throws IOException, ExecutionException, InterruptedException, TimeoutException {
        String[] pos = mc.lookAtPosition();

        System.out.println("Get UserPos " + pos[0] + " " + pos[1] + " " + pos[2]);
        if (pos[0] != null && pos[1] != null && pos[2] != null && pos[3] == "false") {
            ssg.settextFieldColorLayer2(Color.green);
            lookAtPos(Integer.parseInt(pos[0]), Integer.parseInt(pos[1]), Integer.parseInt(pos[2]));

        }
        ssg.settextFieldColorLayer2(Color.red);

    }

    private void mimicry() throws IOException, ExecutionException, TimeoutException, InterruptedException {
        emotion = mc.expressEmotion();
        if (emotion != "none" && emotion != "") {
            ssg.setLayer1Text(emotion);
            ssg.settextFieldColorLayer1(Color.green);
            sendEmotion(emotion);
            if (emotion.equalsIgnoreCase("Happy")) {
                mc.publishEmotionUpdate(0.3f);
            }
            Thread.sleep(2000);
            ssg.settextFieldColorLayer1(Color.red);
            ssg.setLayer1Text("neutral");
            sendEmotion("neutral");

        } else if (emotion == "none") {
            sendEmotion("neutral");
        }

    }

    private void strategic() throws MemoryException, InterruptedException {
        if (mc.strategic()) {
            String[] string = mc.getStrategicReaction();
            if (string[0].equals("OtherQuestion")) {
                System.out.println("Strategic goes on with: " + string[0] + " and has to say " + string[1]);
                mc.say(string[1], "Sad");
                Thread.sleep(1000);
                mc.continueDialog(string[0]);
            } else if (string[0].equals("TriggerEmotion")) {
                System.out.println("Strategic goes on with: " + string[0] + " and has to say " + string[1]);
                mc.say(string[1], "Neutral");
                Thread.sleep(1000);
                mc.continueDialog(string[0]);
            } else {
                System.out.println("Default");
                mc.continueDialog(string[0]);
            }

        }
    }

    private void sendEmotion(String emotion) throws IOException, ExecutionException, TimeoutException, InterruptedException {
        r.executeMovement(hp.getPosition(emotion.toLowerCase()).getActuatorList(), 30, 150);
    }

    private void lookNeutral() throws ExecutionException, IOException, InterruptedException, TimeoutException {
        System.out.println("Look back");
        //cooldown = 0;
        r.executeMovement(hp.getPosition("neutral").getActuatorList(), 30, 150);
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
        this.ssg.addLookAroundListener(new LookAroundActionListener());

        this.ssg.addBlinkListener(new BlinkActionListener());
        this.ssg.addFacialListener(new FacialActionListener());
        this.ssg.addProsodyListener(new ProsodyActionListener());

        this.ssg.addButtonHeadControlListener(new hcGuiListener());

        this.ssg.addbuttonSendImpulsActionListener(new EmotionImpulsActionListener());
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
            startTime = System.currentTimeMillis();

            System.out.println("Starting time: " + startTime);

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

    class LookAroundActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (ssg.getLookAroundStatus()) {
                eth.startExpression(EmotionalExpr.lookAround);
            } else if (!ssg.getLookAroundStatus()) {
                eth.stopExpression(EmotionalExpr.lookAround);
            }

        }
    }

    class ProsodyActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (ssg.getProsodyStatus()) {
                eth.startExpression(EmotionalExpr.prosody);
            } else if (!ssg.getFacialStatus()) {
                eth.stopExpression(EmotionalExpr.prosody);
            }

        }
    }

    class BlinkActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (ssg.getBlinkStatus()) {
                eth.startExpression(EmotionalExpr.blink);
            } else if (!ssg.getFacialStatus()) {
                eth.stopExpression(EmotionalExpr.blink);
                System.out.println("Stop blink");
            }

        }
    }

    class FacialActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (ssg.getFacialStatus()) {
                eth.startExpression(EmotionalExpr.facial);
            } else if (!ssg.getFacialStatus()) {
                eth.stopExpression(EmotionalExpr.facial);
            }

        }
    }

    class hcGuiListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            if (ssg.getButtonHeadControlState()) {
                eg = new HCGui(r, hp);
                eg.setVisible(true);
            } else if (!ssg.getButtonHeadControlState()) {
                eg.setVisible(false);

            }

        }
    }

    class EmotionImpulsActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (ssg.getEmotionalImpuls() != "") {
                mc.publishEmotionUpdate(Float.parseFloat(ssg.getEmotionalImpuls()));
            }
        }

    }

}
