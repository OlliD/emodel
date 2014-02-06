/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.agai.emodel.emotionmain.xcf;

import de.unibi.agai.emodel.emotionmain.Person;
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
public class MemoryConnectorSchematic {

    public RemoteServer rs = null;
    public XcfManager xm;
    private final ActiveMemory am;
    private MemoryEventAdapter memoryEventAdapter;
    private volatile boolean isListening = false;
    private static final Logger LOGGER = Logger.getLogger(MemoryConnector.class.getName());
    private static final String EMOTION_XPATH = "/eModel";
    private boolean eventTrigger = false;
    private String event = "";
    private boolean personReady = false;
    private Person person;
    private int cooldownCounter;
    private int threshold = 15;

    public MemoryConnectorSchematic() throws InitializeException, NameNotFoundException {
        xm = XcfManager.createXcfManager();
        am = xm.createActiveMemory("ShortTerm");
        cooldownCounter = threshold;

    }

    public synchronized void insertToMemory(String elementName, String attributeKey, String attributeValue) throws MemoryException {
        Element root = new Element("eModel");
        root.addAttribute(new Attribute("EModel", "MyMimicry"));
        am.insert(new XOPData(new Document(root)));
        System.out.println("inserted in " + am.getName());

    }

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

    public ActiveMemory getMemory() {
        return am;
    }

    public boolean isListening() {
        return isListening;
    }

    public boolean eventTriggered() {
        return eventTrigger;
    }

    public String getEvent() {
        eventTrigger = false;
        return event;
    }

    public void stopListening() throws MemoryException {
        this.isListening = false;
        am.removeListener(memoryEventAdapter);
    }

    public void insertToMemory() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Person getPerson() {
        if (personReady) {
            cooldownCounter = threshold;
            personReady = false;
            return person;
        } else {
            Person p = new Person(9999, 0, 0, 0);
            return p;
        }

    }
}
