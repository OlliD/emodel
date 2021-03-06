
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.agai.emodel.emotionstrategyselector.xcf;

import de.unibi.agai.eb.BusException;
import de.unibi.agai.emodel.emotionstrategyselector.Controller;
import de.unibi.agai.xtt.TaskCommunicationException;
import de.unibi.agai.xtt.client.TaskSubmissionService;
import de.unibi.agai.xtt.eb.xcf.XcfTaskClientBus;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.xcf.ActiveMemory;
import net.sf.xcf.InitializeException;
import net.sf.xcf.RemoteServer;
import net.sf.xcf.xml.XPath;
import net.sf.xcf.XcfManager;
import net.sf.xcf.event.MemoryEvent;
import net.sf.xcf.event.MemoryEventAdapter;
import net.sf.xcf.memory.MemoryAction;
import net.sf.xcf.memory.MemoryException;
import net.sf.xcf.naming.NameNotFoundException;
import net.sf.xcf.transport.XOPData;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

/**
 *
 * @author odamm
 */
public class MemoryConnector {

    public RemoteServer rs = null;
    public XcfManager xm;
    private ActiveMemory am;
    private MemoryEventAdapter mimircyEventAdapter;
    private MemoryEventAdapter strategicEventAdapter;
    private MemoryEventAdapter schematicEventAdapter;
    private volatile boolean isListening = false;
    private static final Logger LOGGER = Logger.getLogger(MemoryConnector.class.getName());
    private static final String EMOTION_XPATH = "/eModel";
    private boolean expressEmotion;
    private String emotionToExpress = "";
    private String[] position;
    private String[] strategic;
    private boolean lookAtPos = false;
    private boolean strategicEmotion = false;
    private TaskSubmissionService<Document> tss = null;
    private Map<String, String[]> prosody;
    private String[] sad;
    private String[] happy;
    private String[] neutral;

    public MemoryConnector() throws InitializeException, NameNotFoundException, BusException {
        xm = XcfManager.createXcfManager();
        am = xm.createActiveMemory("ShortTerm");
        XcfTaskClientBus clientBus = new XcfTaskClientBus(am);
        tss = new TaskSubmissionService<Document>(clientBus);
        position = new String[4];
        strategic = new String[2];

        sad = new String[3];
        sad[0] = "-30.0%"; //RATE
        sad[1] = "+0.0%"; // RANGE
        sad[2] = "-18.0%"; // PITCH

        neutral = new String[3];
        neutral[0] = "+0.0%";
        neutral[1] = "+0.0%";
        neutral[2] = "+0.0%";

        happy = new String[3];
        happy[0] = "+10.0%";
        happy[1] = "+0.0%";
        happy[2] = "+6.0%";

        prosody = new HashMap<String, String[]>();
        prosody.put("Sad", sad);
        prosody.put("Neutral", neutral);
        prosody.put("Happy", happy);

    }

    public synchronized void insertToMemory() throws MemoryException {
        Element root = new Element("eModel");
        root.addAttribute(new Attribute("EModel", "MyMimicry"));
        am.insert(new XOPData(new Document(root)));
    }

    public synchronized void startListening() throws MemoryException {
        if (!isListening) {
            System.out.println("Now Listening to " + am.getName());
            if (mimircyEventAdapter == null) {
                MemoryAction action = MemoryAction.ALL;

                mimircyEventAdapter = new MemoryEventAdapter(action, new XPath(
                        "//Mimicry")) {

                            @Override
                            synchronized public void handleEvent(MemoryEvent e) {
                                XOPData xml = e.getData();

                                Nodes emotionNodes = xml.getDocument().query(
                                        "//Mimicry");

                                for (int i = 0; i < emotionNodes.size(); i++) {

                                    Node node = emotionNodes.get(i);
                                    if (node instanceof Element) {
                                        Element partElement = (Element) node;
                                        //System.out.println("Found " + partElement.getAttributeValue("Emotion"));
                                        if (!expressEmotion) {
                                            emotionToExpress = partElement.getAttributeValue("Emotion");
                                            expressEmotion = true;
                                        }

                                    }
                                }

                            }
                        };
            }
            if (schematicEventAdapter == null) {
                MemoryAction action = MemoryAction.ALL;

                schematicEventAdapter = new MemoryEventAdapter(action, new XPath(
                        "//Emotion")) {

                            @Override
                            synchronized public void handleEvent(MemoryEvent e) {
                                lookAtPos = false;

                                XOPData xml = e.getData();

                                Nodes emotionNodes = xml.getDocument().query(
                                        "//Schematic");

                                for (int i = 0; i < emotionNodes.size(); i++) {

                                    Node node = emotionNodes.get(i);
                                    if (node instanceof Element) {
                                        Element partElement = (Element) node;
                                        position[0] = partElement.getAttributeValue("X");
                                        position[1] = partElement.getAttributeValue("Y");
                                        position[2] = partElement.getAttributeValue("Z");
                                        position[3] = partElement.getAttributeValue("Player");
                                    }
                                }
                                lookAtPos = true;

                            }

                        };
            }
            if (strategicEventAdapter == null) {
                MemoryAction action = MemoryAction.ALL;

                strategicEventAdapter = new MemoryEventAdapter(action, new XPath(
                        "//Emotion")) {

                            @Override
                            synchronized public void handleEvent(MemoryEvent e) {
                                if (!strategicEmotion) {
                                    XOPData xml = e.getData();

                                    Nodes emotionNodes = xml.getDocument().query(
                                            "//Strategy");

                                    for (int i = 0; i < emotionNodes.size(); i++) {

                                        Node node = emotionNodes.get(i);
                                        if (node instanceof Element) {
                                            Element partElement = (Element) node;
                                            strategic[0] = partElement.getAttributeValue("Continue");
                                            strategic[1] = partElement.getAttributeValue("Say");
                                            strategicEmotion = true;
//                                        }
                                        }
                                    }

                                }
                            }
                        };
            }
        }
    }

    public ActiveMemory getMemory() {
        return am;
    }

    public boolean isListening() {
        return isListening;
    }

    public boolean strategic() {
        return strategicEmotion;
    }

    public String[] getStrategicReaction() {
        strategicEmotion = false;
        return strategic;
    }

    public String[] lookAtPosition() {
        if (lookAtPos = true) {
            lookAtPos = false;
            return position;
        } else {
            return new String[4];
        }

    }

    public String expressEmotion() {
        if (expressEmotion) {
            expressEmotion = false;
            return emotionToExpress;
        } else {
            return "";
        }
    }

    public void publishEmotionUpdate(Float value) {

        Element root = new Element("EmotionalImpulse");

        Element values = new Element("values");
        values.addAttribute(new Attribute("impulse", Float.toString(value)));
        //values.addAttribute(new Attribute("value", Double.toString(value)));
        root.appendChild(values);
        try {
            tss.submit(new Document(root));
        } catch (TaskCommunicationException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 
     * @param utterance - Satz der gesprochen werden soll
     * @param pro - Prosody (Sad / Neutral / Happy)
     * @throws MemoryException 
     */
    
    public synchronized void say(String utterance, String pro) throws MemoryException {
        String[] currentProsody = new String[3];
        currentProsody = prosody.get(pro);
        Element root = new Element("SAY");
        Element xmldata = new Element("xmldata");
        Element utt = new Element("UTTERANCE");
        Element status = new Element("STATUS");
        Element prosodyEle = new Element("prosody");
        prosodyEle.addAttribute(new Attribute("rate", currentProsody[0]));
        prosodyEle.addAttribute(new Attribute("range", currentProsody[1]));
        prosodyEle.addAttribute(new Attribute("pitch", currentProsody[2]));
        prosodyEle.appendChild(utterance);
        status.addAttribute(new Attribute("value", "initiated"));
        status.addAttribute(new Attribute("origin", "Submitter"));
        //utt.appendChild(utterance);
        utt.appendChild(prosodyEle);
        xmldata.appendChild(utt);
        root.appendChild(xmldata);
        root.appendChild(status);
        am.insert(new XOPData(new Document(root)));

    }

 public synchronized void continueDialog(String msg) throws MemoryException {
        Element root = new Element("GameDialog");
        root.addAttribute(new Attribute("Typ", "Strategic"));
        Element ele = new Element("Strategy");
        root.appendChild(ele);
        ele.addAttribute(new Attribute("Continue", msg));

        am.insert(new XOPData(new Document(root)));

    }


    public void startListeningLayer1(boolean status) throws MemoryException {
        if (status) {
            am.addListener(mimircyEventAdapter);
            System.err.println("ESS:MemoryConnector - mimicry inserted");
            isListening = true;
        } else {
            am.removeListener(mimircyEventAdapter);
            isListening = false;
        }
    }

    public void startListeningLayer2(boolean status) throws MemoryException {
        if (status) {
            am.addListener(schematicEventAdapter);
            System.err.println("ESS:MemoryConnector - schematic inserted");
            isListening = true;
        } else {
            am.removeListener(schematicEventAdapter);
            isListening = false;
        }
    }

    public void startListeningLayer3(boolean status) throws MemoryException {
        if (status) {
            am.addListener(strategicEventAdapter);
            System.err.println("ESS:MemoryConnector - strategic inserted");
            isListening = true;
        } else {
            am.removeListener(strategicEventAdapter);
            isListening = false;
        }
    }

    void stopListening() throws MemoryException {
        if (isListening) {
            am.removeListener(mimircyEventAdapter);
            am.removeListener(schematicEventAdapter);
            am.removeListener(strategicEventAdapter);
            isListening = false;
        }
    }
}
