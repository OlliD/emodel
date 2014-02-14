/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.agai.emodel.emotionmain;

import de.unibi.agai.emodel.emotionmain.types.Persons;
import de.unibi.agai.emodel.emotionmain.types.Face;
import de.unibi.agai.emodel.emotionmain.types.Person;
import com.sun.jmx.snmp.Timestamp;
import de.unibi.agai.eb.BusException;
import de.unibi.agai.emodel.emotionmain.xcf.MemoryConnector;
import de.unibi.agai.emodel.emotionmain.xcf.MemoryConnectorSchematic;
import de.unibi.agai.emodel.gui.EmotionMainGui;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.xcf.ActiveMemory;
import net.sf.xcf.InitializeException;
import net.sf.xcf.XcfManager;
import net.sf.xcf.memory.MemoryException;
import net.sf.xcf.naming.NameNotFoundException;

/**
 *
 * @author odamm
 */
public class ControllerEmotionMain {

    private XcfManager xm;
    private ActiveMemory am;
    private ActiveMemory memory_ShortTerm;
    private ActiveMemory memory_Vision;

    private MemoryConnector speechConnector;
    private MemoryConnector contextConnector;
    private MemoryConnector faceConnector;

    private MemoryConnectorSchematic bodyConnector;
    //private Map<String, Float> emotions;
    private Float threshold = 50f;
    private List<Face> faceList;
    private Person p;
    private Persons persons;
    private EmotionMainGui gui;
    private boolean run = false;

    public ControllerEmotionMain() throws InitializeException, NameNotFoundException, InterruptedException, MemoryException, BusException {

        // Initialize the GUI
        gui = new EmotionMainGui();
        gui.setVisible(true);

        addActionListener();

        System.out.println("Current Time " + System.currentTimeMillis());

//        emotions = new HashMap<String, Float>();
        persons = new Persons();
        this.worker();

        //WASBAI 
        //EmotionTaskHandler emoHandler = new EmotionTaskHandler();
        //emoHandler.start();
    }

    private void addActionListener() {
        gui.addButtonContextEventsListener(new contextEventsListener());
        gui.addButtonFaceEventsListener(new faceEventsListener());
        gui.addCheckBoxConnectToMemoryListener(new ConnectToMemoryListener());
        gui.addButtonStartListener(new startListener());
        gui.addButtonStopListener(new stopListener());
    }

    private void worker() throws MemoryException {

        int j = 0;
        new Thread() {
            @Override
            public void run() {
                while (run) {
                    try {
//                        emotions = faceConnector.getEmotionMap();
                        // Frage beim Connector ob ein neues Gesicht gibt
                        if (faceConnector.faceListReady()) {
                            updateFaceList(faceConnector.getFace());
                        }
                        if (!faceList.isEmpty()) {
                            speechConnector.insertToMemory("Facial", getFirstFace().getMostLikelyEmotion(), Float.toString(getFirstFace().getReliability(getFirstFace().getMostLikelyEmotion())));
                        }

                        // Frage beim Connector ob ein neuer Körper im Bild ist
                        
                        if (bodyConnector.personReady()) {
                            updateBodyList(bodyConnector.getPerson());
                        }
                        
                        Thread.sleep(500);

                        //persons.printList();
                       /*
                         if (persons.playerDetected()) {
                         bodyConnector.insertToMemory("Position", persons.getPlayer()); // change to getOther
                         }*/
                        if (persons.otherPerson()) {
                            System.out.println("OTHER: ");
                            persons.getOther().print();
//                           System.out.println("Distance: " + persons.distance(persons.getPlayer(), persons.getOther()));
                            bodyConnector.insertToMemory("Position", persons.getOther()); // change to getOther
                        }
                        // Die Werte fuer die Reliability jeder Emotion wird in die GUI geschrieben
                        gui.setEmotionValues(getFirstFace().getCurrentId(),
                                getFirstFace().getEmotionByName("Happy"),
                                getFirstFace().getEmotionByName("Angry"),
                                getFirstFace().getEmotionByName("Sad"),
                                getFirstFace().getEmotionByName("Surprised"));

                        //System.out.println("Most Likely Emotion: " + getFirstFace().getMostLikelyEmotion() + " " + getFirstFace().getReliability(getFirstFace().getMostLikelyEmotion()));
                        //Looking for the emotion map and receive a map of emotions (happy, sad, surprised, angry) with value for reliability
                        //when one value is over the threshold the label will be inserted into the memory
                        cleanUpFaceList();
                        cleanUpBodyList();

                        /*
                         System.out.println("### WORKER ### After Cleaning " + faceList.size() + " faces left");
                         for (int i = 0; i < faceList.size(); i++) {
                         System.out.println("### WORKER ### id " +i);
                         faceList.get(i).printFace();
                         }
                         System.out.println("### WORKER ### Iteration " + j);
                         System.out.println("");
                         j++;
                         */
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ControllerEmotionMain.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (MemoryException ex) {
                        Logger.getLogger(ControllerEmotionMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        }.start();

    }

    public List<Face> getFaceList() {
        return faceList;
    }

    public void setFaceList(List<Face> faceList) {
        this.faceList = faceList;
    }

    public void addFace(Face face) {
        this.faceList.add(face);
    }

    // Füge das neue Gesicht in die Liste 
    /*
     public void showFacialEmotionReliability() {
     for (Map.Entry<String, Float> entry : emotions.entrySet()) {
     if (entry.getValue() > threshold) {
     //System.out.println("EmotionMain: " + entry.getKey() + " = " + entry.getValue());
     //       speechConnector.insertToMemory("mimicry", entry.getKey(), entry.getValue().toString());
     //     speechConnector.insertToMemory("schematic", entry.getKey(), entry.getValue().toString());

     emotions.put(entry.getKey(), 0f);

     }
     }
     }
     */
    public void updateFaceList(List<Face> detectedFaces) {
        boolean updateFaces = false;
        boolean addFace = false;
        if (faceList.isEmpty()) {
            faceList.addAll(detectedFaces);

        } else {
            //System.out.println("EMotionMain: Received in total " + detectedFaces.size());
            for (int j = 0; j < detectedFaces.size(); j++) {

                Face f = detectedFaces.get(j);

                for (int i = 0; i < faceList.size(); i++) {
                    if (faceList.get(i).getCurrentId() != f.getCurrentId()) {
                        updateFaces = false;
                        addFace = true;
                    } else if (faceList.get(i).getCurrentId() == f.getCurrentId()) {
                        faceList.get(i).setCurrentId(f.getCurrentId());
                        faceList.get(i).setLastId(f.getLastId());
                        faceList.get(i).setTimpStamp(f.getTimpStamp());
                        faceList.get(i).setViewCount(f.getViewCount());
                        faceList.get(i).setEmotions(f.getEmotions());
                        //System.out.println("EMotionMain: ID " + faceList.get(i).getCurrentId() + " updated");
                        addFace = false;
                        break;
                    }
                }
                if (addFace) {
                    faceList.add(f);
                    System.out.println("EMotionMain: ID " + f.getCurrentId() + " added");
                    addFace = false;
                }
            }
        }
        //System.out.println("EMotionMain: Now are " + faceList.size() + " items in List ####");

    }

    public Face getFirstFace() {
        if (!faceList.isEmpty()) {
            Face f = faceList.get(0);
            for (int i = 0; i < faceList.size(); i++) {
                if (f.getCurrentId() > faceList.get(i).getCurrentId()) {
                    f = faceList.get(i);
                }
            }
            return f;
        } else {
            return new Face(999);
        }
    }

    public void cleanUpFaceList() {
        for (int i = 0; i < faceList.size(); i++) {
            if ((faceList.get(i).getTimpStamp() + 15000) < System.currentTimeMillis()) {
                faceList.remove(faceList.get(i));
            }
        }
        //System.out.println("After CleanUp there are " + faceList.size() + " in List");
    }

    public void pushFacialEmotion() {

    }

    public void pushContextEvent() {

    }

    public void updateBodyList(Person p) {
        if (p.getId() != 9999) {
            persons.addNewPerson(p);
        }
    }

    public void cleanUpBodyList() {
        if (persons.getPlayer() != null) {
            gui.setPositionX(persons.getPlayer().getX());
            gui.setPositionY(persons.getPlayer().getY());
            gui.setPositionZ(persons.getPlayer().getZ());
        }

        for (int i = 0; i < persons.getSize(); i++) {
            if (((persons.get(i).getDetected()) + 5000) < System.currentTimeMillis()) {
                persons.printList();
                persons.remove(i);
                persons.printList();
            }
        }
        //System.out.println(" Currently in PersonList " + persons.getSize());
    }

    class faceEventsListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            try {
                //Empfange neue Gesichter / Emotion vom Shore-Erkenner
                if (!faceConnector.isListening()) {
                    faceConnector.startListening();
                } else if (faceConnector.isListening()) {
                    faceConnector.stopListening();
                }
            } catch (MemoryException ex) {
                Logger.getLogger(ControllerEmotionMain.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Listen to Shore");
        }
    }

    class contextEventsListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            try {
                //Empfange neue Körper vom BodyDetector
                if (!bodyConnector.isListening()) {
                    bodyConnector.startListening("/PERCEPTS");
                } else if (bodyConnector.isListening()) {
                    bodyConnector.stopListening();
                }
            } catch (MemoryException ex) {
                Logger.getLogger(ControllerEmotionMain.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Listen to Kinect");
        }
    }

    class startListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            run = true;
            try {
                worker();
            } catch (MemoryException ex) {
                Logger.getLogger(ControllerEmotionMain.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    class stopListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            run = false;
            System.out.println("STOP");
        }
    }

    class ConnectToMemoryListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            try {

                xm = XcfManager.createXcfManager();
                memory_ShortTerm = xm.createActiveMemory("ShortTerm");
                memory_Vision = xm.createActiveMemory("vision");

                //ISR
                speechConnector = new MemoryConnector("speech", memory_ShortTerm);

                // Shore
                faceConnector = new MemoryConnector("OBJECTS", memory_Vision);
                faceList = new ArrayList<Face>();
                // Kinect
                bodyConnector = new MemoryConnectorSchematic();

                System.out.println("EmotionMain: Connected to Mem");
            } catch (InitializeException ex) {
                Logger.getLogger(ControllerEmotionMain.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NameNotFoundException ex) {
                Logger.getLogger(ControllerEmotionMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
