/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.agai.emodel.emotionmain;

import com.sun.jmx.snmp.Timestamp;
import de.unibi.agai.eb.BusException;
import de.unibi.agai.emodel.emotionmain.xcf.MemoryConnector;
import de.unibi.agai.emodel.emotionmain.xcf.MemoryConnectorSchematic;
import de.unibi.agai.emotionlib.communication.EmotionTaskHandler;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.sf.xcf.ActiveMemory;
import net.sf.xcf.InitializeException;
import net.sf.xcf.XcfManager;
import net.sf.xcf.memory.MemoryException;
import net.sf.xcf.naming.NameNotFoundException;

/**
 *
 * @author odamm
 */
public class Controller {

    private XcfManager xm;
    private ActiveMemory am;
    private ActiveMemory am_ST;
    private ActiveMemory am_V;
    private MemoryConnector speechConnector;
    private MemoryConnector faceConnector;
    private MemoryConnectorSchematic bodyConnector;
    private Map<String, Float> emotions;
    private Float threshold = 50f;
    private List<Faces> faceList;
    private Timestamp tstamp;
    private Person p;
    private Persons persons;

    public Controller() throws InitializeException, NameNotFoundException, InterruptedException, MemoryException, BusException {
        XcfManager xm = XcfManager.createXcfManager();
        ActiveMemory am_ST = xm.createActiveMemory("ShortTerm");
        ActiveMemory am_V = xm.createActiveMemory("vision");
        tstamp = new Timestamp(System.currentTimeMillis());
        System.out.println("Current Time " + tstamp.getDate().getTime() / 1000);
        
        //ISR
        speechConnector = new MemoryConnector("speech", am_ST);
        
        // Shore
        faceConnector = new MemoryConnector("OBJECTS", am_V);
        emotions = new HashMap<String, Float>();
        faceList = new ArrayList<Faces>();
        // Kinect
        bodyConnector = new MemoryConnectorSchematic();
        persons = new Persons();

        //WASBAI 
        EmotionTaskHandler emoHandler = new EmotionTaskHandler();
        emoHandler.start();

        this.worker();
    }

    private void worker() throws InterruptedException, MemoryException {
        //Empfange neue Gesichter / Emotion vom Shore-Erkenner
        faceConnector.startListening();
        //Empfange neue Körper vom BodyDetector
        bodyConnector.startListening("/PERCEPTS");

        int j = 0;
        while (true) {
            Thread.sleep(2000);
            emotions = faceConnector.getEmotionMap();
            // Frage beim Connector ob ein neues Gesicht gibt
            updateFaceList(faceConnector.getFace());
            // Frage beim Connector ob ein neuer Körper im Bild ist
            updateBodyList(bodyConnector.getPerson());

            //Looking for the emotion map and receive a map of emotions (happy, sad, surprised, angry) with value for reliability
            //when one value is over the threshold the label will be inserted into the memory
            for (Map.Entry<String, Float> entry : emotions.entrySet()) {
                if (entry.getValue() > threshold) {
                    System.out.println("EmotionMain: " + entry.getKey() + " = " + entry.getValue());
                    speechConnector.insertToMemory("mimicry", entry.getKey(), entry.getValue().toString());
                    speechConnector.insertToMemory("schematic", entry.getKey(), entry.getValue().toString());

                    emotions.put(entry.getKey(), 0f);

                }
            }
            /*
             //           cleanUpFaceList();
             System.out.println("### WORKER ### After Cleaning " + faceList.size() + " faces left");
             for (int i = 0; i < faceList.size(); i++) {
             System.out.println("### WORKER ### id " +i);
             faceList.get(i).printFace();
             }
             System.out.println("### WORKER ### Iteration " + j);
             System.out.println("");
             j++;
             */
        }

    }

    public List<Faces> getFaceList() {
        return faceList;
    }

    public void setFaceList(List<Faces> faceList) {
        this.faceList = faceList;
    }

    public void addFace(Faces face) {
        this.faceList.add(face);
    }

    // Füge das neue Gesicht in die Liste 
    public void updateFaceList(List<Faces> detectedFaces) {

        boolean updateFaces = false;
        boolean addFace = false;
        if (faceList.size() == 0) {
            faceList.addAll(detectedFaces);

        } else {
            System.out.println("EMotionMain: Received in total " + detectedFaces.size());
            for (int j = 0; j < detectedFaces.size(); j++) {

                Faces f = detectedFaces.get(j);

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
                        System.out.println("EMotionMain: ID " + faceList.get(i).getCurrentId() + " updated");
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
        System.out.println("EMotionMain: Now are " + faceList.size() + " items in List ####");

    }

    public void cleanUpFaceList() {
        for (Faces f : faceList) {
            if (f.getTimpStamp() < tstamp.getDate().getTime() + 20000) {
                faceList.remove(f);
            }
        }
    }

    public void updateBodyList(Person p) {
        if (p.getId() != 9999) {
            persons.addNewPerson(p);
        }
    }

    public void cleanUpBodyList() {

    }
}
