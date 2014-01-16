/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.unibi.agai.emodel.emotionmain.xcf;

import java.util.HashMap;
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

        
        public MemoryConnector(String xpath, ActiveMemory mem) throws InitializeException, NameNotFoundException {
            this.am = mem;
            //xm = XcfManager.createXcfManager();
            //am = xm.createActiveMemory("ShortTerm");
            this.xpath = xpath;
            emotions = new HashMap<String, Float>();
            emotions.put("Happy", 0f);
            emotions.put("Angry", 0f);
            emotions.put("Sad", 0f);
            emotions.put("Surprised", 0f);

	} 

        public synchronized void startListening() throws MemoryException {
		
            if (!isListening) {
                    if (EventAdapter == null){
				MemoryAction action = MemoryAction.INSERT;

				EventAdapter = new MemoryEventAdapter(action, new XPath(
						"/"+xpath)) {

					@Override
					synchronized public void handleEvent(MemoryEvent e) {
                                                XOPData xml = e.getData();

						Nodes emotionNodes = xml.getDocument().query(
								"//OBJECT/ATTRIBUTES[@creator]/ATTRIBUTE[@name=\"Emotion\"]/RELIABILITY");
						// Get the phoneme chain text
						for (int i = 0; i < emotionNodes.size(); i++) {
                                                        //System.out.println("SIze " + emotionNodes.size());
							Node node = emotionNodes.get(i);
							if (node instanceof Element) {
								Element partElement = (Element) node;
                                                                //System.out.println("found " + partElement.getAttributeValue("name") + "with " +partElement.getAttributeValue("value"));
                                                                float value = Float.parseFloat(partElement.getAttributeValue("value"));
                                                                //System.out.println(value);
                                                                emotions.put(partElement.getAttributeValue("name"), value);
                                                        } 
						}

					}
				};
			}
                         
                        System.out.println("STARTET Listening to " + am.getName() + " for /" + xpath + " events");

			am.addListener(EventAdapter);
			isListening = true;
		}
	}
        
        public ActiveMemory getMemory (){
            return am;
        }
        
        public boolean isListening() {
		return isListening;
	}

        public void stopListening() throws MemoryException {
        if (this.isListening){
            am.removeListener(EventAdapter);
            System.out.println("STOPPED Listening to " + am.getName() + " for /" + xpath + " events");
            isListening = false;

        }
           
      }
        public Map<String, Float> getEmotionMap(){
            return emotions;
            
        }
        public synchronized void insertToMemory(String elementName, String attributeKey, String attributeValue) throws MemoryException{
            Element root = new Element(elementName);
            root.addAttribute(new Attribute("Emotion", attributeKey));
            root.addAttribute(new Attribute("Reliability", attributeValue));
            System.err.println("EmotionMain: " +root.toString());
            am.insert(new XOPData(new Document(root)));
        }

}



    

