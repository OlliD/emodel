/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.agai.emodel.emotionschematic.xcf;

import de.unibi.agai.emodel.emotionschematic.Person;
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
    private final ActiveMemory am;
    private MemoryEventAdapter memoryEventAdapter;
    private volatile boolean isListening = false;
    private static final Logger LOGGER = Logger.getLogger(MemoryConnector.class.getName());
    //private static final String EMOTION_XPATH = "/eModel";
    private boolean eventTrigger = false;
    //private String event = "";
    private boolean personReady = false;
    private Person person;
    private int cooldownCounter;
    private int threshold = 15;
    private String[] position;

    public MemoryConnector() throws InitializeException, NameNotFoundException {
        xm = XcfManager.createXcfManager();
        am = xm.createActiveMemory("ShortTerm");
        cooldownCounter = threshold;
        position = new String[4];

    }

    public synchronized void insertToMemory(String elementName, String attributeKey, String attributeValue) throws MemoryException {
        Element root = new Element("eModel");
        root.addAttribute(new Attribute("EModel", "MyMimicry"));
        am.insert(new XOPData(new Document(root)));
        System.out.println("inserted in " + am.getName());

    }
    /*
     public synchronized void startListening(String xpath) throws MemoryException {
     if (!isListening) {
     System.out.println("Now Listening to " + am.getName());
     if (memoryEventAdapter == null) {
     MemoryAction action = MemoryAction.REPLACE;

     memoryEventAdapter = new MemoryEventAdapter(action, new XPath(
     xpath)) {

     @Override
     synchronized public void handleEvent(MemoryEvent e) {
     cooldownCounter--;
     if (cooldownCounter == 0) {
     System.out.println("Pick Up The Person");
     personReady = false;
     XOPData xml = e.getData();
     Nodes bodyNodes = xml.getDocument().query("//BODYSKELETON");
     double x = 0;
     double y = 0;
     double z = 0;
     int id = 0;
     for (int i = 0; i < bodyNodes.size(); i++) {
     Node bodyNode = bodyNodes.get(i);
     Element bodyElement = (Element) bodyNode;
     id = Integer.parseInt(bodyElement.getAttributeValue("id"));
     Nodes comNodes = bodyNode.query("//COM");
     for (int j = 0; j < comNodes.size(); j++) {
     Node node = comNodes.get(j);
     Element partElement = (Element) node;
     x = Double.parseDouble(partElement.getAttributeValue("x"));
     y = Double.parseDouble(partElement.getAttributeValue("y"));
     z = Double.parseDouble(partElement.getAttributeValue("z"));
     }
     }
     person = new Person(id, (int) x, (int) y, (int) z);
     personReady = true;
     cooldownCounter = threshold;

     } else {
     personReady = true;

     }

     }
     };
     }

     am.addListener(memoryEventAdapter);
     isListening = true;
     }
     }
     */

    public synchronized void startListening(String inputSelector) throws MemoryException {
        final String is = inputSelector;
        if (!isListening) {
            if (memoryEventAdapter == null) {
                MemoryAction action = MemoryAction.INSERT;

                memoryEventAdapter = new MemoryEventAdapter(action, new XPath(
                        "//Emotion/Position")) {

                            @Override
                            synchronized public void handleEvent(MemoryEvent e) {
                                if (!personReady) {
                                    //personReady = false;

                                    XOPData xml = e.getData();

                                    Nodes emotionNodes = xml.getDocument().query(
                                            "//Emotion/Position");

                                    System.out.println(xml.toString() + " " + emotionNodes.size());
                                    for (int i = 0; i < emotionNodes.size(); i++) {
                                        Node node = emotionNodes.get(i);
                                        System.out.println(node.toXML());
                                        if (node instanceof Element) {
                                            Element partElement = (Element) node;
                                            position[0] = partElement.getAttributeValue("X");
                                            position[1] = partElement.getAttributeValue("Y");
                                            position[2] = partElement.getAttributeValue("Z");
                                            position[3] = partElement.getAttributeValue("Player");

                                        }
                                    }
                                    personReady = true;

                                    System.out.println("Person at " + position[0] + " " + position[1] + " " + position[2] + position[3]);

                                    eventTrigger = true;
                                }
                            }
                        };
            }
            System.out.println("Now Listening to " + am.getName() + " for /" + is + " events");

            am.addListener(memoryEventAdapter);
            isListening = true;
        }
    }

    public ActiveMemory getMemory() {
        return am;
    }

    public boolean isListening() {
        return isListening;
    }

    public boolean personReady() {
        return personReady;
    }

    public String[] getCoordinates() {
        //    String[] pos;
        //    if (personReady) {
        //        cooldownCounter = threshold;
        personReady = false;
        return position;
        //    } else {
        //        pos = new String[3];
        //        return pos;
        //    }
    }

    /*
     public boolean eventTriggered() {
     return eventTrigger;
     }

     public String getEvent() {
     eventTrigger = false;
     return event;
     }
     */
    public void stopListening() throws MemoryException {
        this.isListening = false;
        am.removeListener(memoryEventAdapter);
    }

    public synchronized void interuptDialog(String msg) throws MemoryException {
        Element root = new Element("Emotion");
        root.addAttribute(new Attribute("Typ", "Strategic"));
        Element ele = new Element("Strategic");
        root.appendChild(ele);
        ele.addAttribute(new Attribute("Flobi", "24"));
        ele.addAttribute(new Attribute("Human", "22"));
        am.insert(new XOPData(new Document(root)));
    }

    public synchronized void insertToMemory(String elementName, String[] position) throws MemoryException {
        Element root = new Element("Emotion");
        Element ele = new Element(elementName);
        root.appendChild(ele);
        String x = position[0];
        String y = position[1];
        String z = position[2];
        ele.addAttribute(new Attribute("X", x));
        ele.addAttribute(new Attribute("Y", y));
        ele.addAttribute(new Attribute("Z", z));

        am.insert(new XOPData(new Document(root)));
    }

    public synchronized void say(String utterance, String p, String a, String d) throws MemoryException {
        Element root = new Element("SAY");
        Element xmldata = new Element("xmldata");
        Element utt = new Element("UTTERANCE");
        Element status = new Element("STATUS");
        status.addAttribute(new Attribute("value", "initiated"));
        status.addAttribute(new Attribute("origin", "Submitter"));
        utt.appendChild(utterance);
        xmldata.appendChild(utt);
        Element pad = new Element("PAD");
        pad.addAttribute(new Attribute("pleasure", p));
        pad.addAttribute(new Attribute("arousal", a));
        pad.addAttribute(new Attribute("dominance", d));
        root.appendChild(xmldata);
        root.appendChild(pad);
        root.appendChild(status);
        am.insert(new XOPData(new Document(root)));

    }

}
