/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.agai.emodel.emotionstrategic.xcf;

import de.unibi.agai.emodel.emotionstrategic.types.GameState;
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
    private static final String EMOTION_XPATH = "/eModel";
    private boolean gameStateReady = false;
    private GameState gm;

    public MemoryConnector() throws InitializeException, NameNotFoundException {
        xm = XcfManager.createXcfManager();
        am = xm.createActiveMemory("ShortTerm");

    }

    public synchronized void insertToMemory(String elementName, String attributeKey, String attributeValue) throws MemoryException {
        Element root = new Element("eModel");
        root.addAttribute(new Attribute("EModel", "MyMimicry"));
        am.insert(new XOPData(new Document(root)));
        System.out.println("inserted in " + am.getName());

    }

    public synchronized void startListening() throws MemoryException {

        if (!isListening) {
            System.out.println("Now Listening to " + am.getName());
            if (memoryEventAdapter == null) {
                MemoryAction action = MemoryAction.INSERT;

                memoryEventAdapter = new MemoryEventAdapter(action, new XPath(
                        "//Emotion[@Typ=\"Strategic\"]")) {
                            @Override
                            synchronized public void handleEvent(MemoryEvent e) {
                                if (!gameStateReady) {
                                    System.out.println("Found");
                                    XOPData xml = e.getData();
                                    Nodes emotionNodes = xml.getDocument().query(
                                            "//Emotion[@Typ=\"Strategic\"]/Strategic");
                                    for (int i = 0; i < emotionNodes.size(); i++) {
                                        Node node = emotionNodes.get(i);
                                        if (node instanceof Element) {
                                            Element partElement = (Element) node;
                                            if (partElement.getAttribute("Human") != null) {
                                                int human = Integer.parseInt(partElement.getAttributeValue("Human"));
                                                int flobi = Integer.parseInt(partElement.getAttributeValue("Flobi"));
                                                gm = new GameState(human, flobi);
                                                gameStateReady = true;

                                            }
                                        }
                                    }
                                }
                            }
                        };
            }

            am.addListener(memoryEventAdapter);
            isListening = true;
        }
    }

    public synchronized void continueDialog(String msg, String sentence) throws MemoryException {
        Element root = new Element("Emotion");
        root.addAttribute(new Attribute("Typ", "Strategic"));
        Element ele = new Element("Strategy");
        root.appendChild(ele);
        ele.addAttribute(new Attribute("Continue", msg));
        ele.addAttribute(new Attribute("Say", sentence));

        am.insert(new XOPData(new Document(root)));

    }

    public ActiveMemory getMemory() {
        return am;
    }

    public boolean isListening() {
        return isListening;
    }

    public void stopListening() throws MemoryException {
        this.isListening = false;
        am.removeListener(memoryEventAdapter);
    }

    public GameState getGameState() {
        this.gameStateReady = false;

        return gm;
    }

    public boolean gameStateReady() {
        return gameStateReady;
    }
}
