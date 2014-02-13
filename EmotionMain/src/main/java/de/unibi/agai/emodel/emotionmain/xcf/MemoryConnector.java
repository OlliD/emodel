/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.agai.emodel.emotionmain.xcf;

import de.unibi.agai.emodel.emotionmain.types.Face;
import de.unibi.agai.emodel.emotionmain.types.Person;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private MemoryEventAdapter EventAdapter;
    private volatile boolean isListening = false;
    private static final Logger LOGGER = Logger.getLogger(MemoryConnector.class.getName());
    private static final String EMOTION_XPATH = "/eModel";
    private String xpath = "";
    private Map<String, Float> emotions;
    private Face face;
    private List<Face> faceList;
    boolean faceReady = false;
    
    public MemoryConnector(String xpath, ActiveMemory mem) throws InitializeException, NameNotFoundException {
        this.am = mem;
        //xm = XcfManager.createXcfManager();
        //am = xm.createActiveMemory("ShortTerm");
        this.xpath = xpath;
        faceList = new ArrayList<Face>();
        emotions = new HashMap<String, Float>();
        
        emotions.put("Happy", 0f);
        emotions.put("Angry", 0f);
        emotions.put("Sad", 0f);
        emotions.put("Surprised", 0f);

    }

    public synchronized void startListening() throws MemoryException {
        
        if (!isListening) {
            if (EventAdapter == null) {
                MemoryAction action = MemoryAction.INSERT;

                EventAdapter = new MemoryEventAdapter(action, new XPath(
                        "/" + xpath)) {

                            @Override
                            synchronized public void handleEvent(MemoryEvent e) {
                                XOPData xml = e.getData();
                                faceReady = false;
                                faceList.clear();
                                Nodes objectNodes =  xml.getDocument().query("/OBJECTS/OBJECT");
                                for (int j = 0; j < objectNodes.size(); j++) {
                                    
                                    Node currentNode = objectNodes.get(j);
                                    
                                    Document doc = new Document((Element) currentNode.copy());
                                    
                                    Nodes timeNodes = doc.query("//OBJECT/TIMESTAMPS/UPDATED[@ms]");
                                    Node timeNode = timeNodes.get(0);
                                    Element element = (Element) timeNode;
                                    long timpStamp = Long.parseLong(element.getAttributeValue("ms"));
                                    Nodes faceCountNodes = doc.query(
                                            "//OBJECT/CLASS[@Id]");
                                    int id=0;
                                    
                                    for (int i = 0; i < faceCountNodes.size(); i++) {
                                        Node node = faceCountNodes.get(i);
                                        Element partElement = (Element) node;
                                        //System.out.println("found ID: " + partElement.getAttributeValue("Id") + " found prevID: " + partElement.getAttributeValue("PreviousIds"));
                                        String str = partElement.getAttributeValue("Id");
                                        id = Integer.parseInt(partElement.getAttributeValue("Id"));
                                    }
                                    face = new Face(id);
                                    face.setTimpStamp(timpStamp);
                                    
                                    Nodes emotionNodes = doc.query(
                                            "//ATTRIBUTES[@creator]/ATTRIBUTE[@name=\"Emotion\"]/RELIABILITY");
                                    
                                    for (int i = 0; i < emotionNodes.size(); i++) {
                                        Node node = emotionNodes.get(i);
                                        if (node instanceof Element) {
                                            Element partElement = (Element) node;
                                            //System.out.println("found " + partElement.getAttributeValue("name") + " with " + partElement.getAttributeValue("value"));
                                            float value = Float.parseFloat(partElement.getAttributeValue("value"));
                                            emotions.put(partElement.getAttributeValue("name"), value);
                                            face.addEmotion(partElement.getAttributeValue("name"), Float.parseFloat(partElement.getAttributeValue("value")));
                                        }

                                    }
                                    //face.printFace();
                                    faceList.add(face);
                                }
                                faceReady = true;
                            }
                        };
            }

            System.out.println("STARTET Listening to " + am.getName() + " for /" + xpath + " events");
            am.addListener(EventAdapter);
            isListening = true;
        }
    }

    public ActiveMemory getMemory() {
        return am;
    }

    public boolean isListening() {
        return isListening;
    }

    public void stopListening() throws MemoryException {
        if (this.isListening) {
            am.removeListener(EventAdapter);
            System.out.println("STOPPED Listening to " + am.getName() + " for /" + xpath + " events");
            isListening = false;

        }
    }

    public Map<String, Float> getEmotionMap() {
        return emotions;
    }
    
    public List<Face> getFace(){
        if (faceReady){
            //System.out.println("Sending List with " + faceList.size() + " items");
            return faceList;
        }
        else {
            List<Face> faceListDummy = new ArrayList<Face>();
            return faceListDummy;
        }
            
    }

    public synchronized void insertToMemory(String elementName, String attributeKey, String attributeValue) throws MemoryException {
        Element root = new Element("Emotion");
        Element ele = new Element(elementName);
        root.appendChild(ele);
        Object obj = new Person(1,1,1,1,true);
        
        ele.addAttribute(new Attribute("Emotion", attributeKey));
        ele.addAttribute(new Attribute("Reliability", attributeValue));
        am.insert(new XOPData(new Document(root)));
    }
}
