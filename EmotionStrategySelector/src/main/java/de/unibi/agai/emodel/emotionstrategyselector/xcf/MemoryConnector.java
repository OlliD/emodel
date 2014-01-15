/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.unibi.agai.emodel.emotionstrategyselector.xcf;


import de.unibi.agai.emodel.emotionstrategyselector.gui.StrategySelectorGui;
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
import net.sf.xcf.util.SynchronizedQueue;
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
        private StrategySelectorGui ssg; 
        
        public MemoryConnector(StrategySelectorGui ssg) throws InitializeException, NameNotFoundException {
            xm = XcfManager.createXcfManager();
            am = xm.createActiveMemory("ShortTerm");
            this.ssg = ssg;

            
            
	} 


        public synchronized void insertToMemory() throws MemoryException{
            Element root = new Element("eModel");
            root.addAttribute(new Attribute("EModel", "MyMimicry"));
            am.insert(new XOPData(new Document(root)));
            System.out.println("inserted in " + am.getName());

        }
        
        public synchronized void listenToMemory(){
            try {
                System.out.println("lets take a look in " + am.getName());
                am.addListener(new MemoryEventAdapter(
		MemoryAction.ALL, new XPath("/*")) {
			synchronized public void handleEvent(
			MemoryEvent e) {
				System.out.println(" found something "+ e.getData()
					.getDocumentAsText());
			}});
                
	} catch(MemoryException e) {
                }
        }
        
        
        public synchronized void startListening() throws MemoryException {
		
            if (!isListening) {
                    System.out.println("Now Listening to " + am.getName());
                    if (mimircyEventAdapter == null){
				MemoryAction action = MemoryAction.INSERT;

				mimircyEventAdapter = new MemoryEventAdapter(action, new XPath(
						"/mimicry")) {

					@Override
					synchronized public void handleEvent(MemoryEvent e) {
                                                XOPData xml = e.getData();

						Nodes emotionNodes = xml.getDocument().query(
								"/mimicry");

						// Get the phoneme chain text
						for (int i = 0; i < emotionNodes.size(); i++) {

							Node node = emotionNodes.get(i);
							if (node instanceof Element) {
								Element partElement = (Element) node;
                                                                System.out.println("Mimicry Event: "  + partElement.getAttributeValue("emotion"));  
                                                                ssg.setLayer1Text(partElement.getAttributeValue("emotion"));

                                                        } 
						}

					}
				};
			}
                        if (schematicEventAdapter == null){
				MemoryAction action = MemoryAction.INSERT;

				schematicEventAdapter = new MemoryEventAdapter(action, new XPath(
						"/schematic")) {

					@Override
					synchronized public void handleEvent(MemoryEvent e) {
                                                XOPData xml = e.getData();

						Nodes emotionNodes = xml.getDocument().query(
								"/schematic");

						// Get the phoneme chain text
						for (int i = 0; i < emotionNodes.size(); i++) {

							Node node = emotionNodes.get(i);
							if (node instanceof Element) {
								Element partElement = (Element) node;
                                                                System.out.println("Schematic Event: " + partElement.getAttributeValue("emotion"));
                                                                ssg.setLayer2Text(partElement.getAttributeValue("emotion"));

                                                        } 
						}

					}
				};
			}
                        if (strategicEventAdapter == null){
				MemoryAction action = MemoryAction.INSERT;

				strategicEventAdapter = new MemoryEventAdapter(action, new XPath(
						"/strategic")) {

					@Override
					synchronized public void handleEvent(MemoryEvent e) {
                                                XOPData xml = e.getData();

						Nodes emotionNodes = xml.getDocument().query(
								"/strategic");

						// Get the phoneme chain text
						for (int i = 0; i < emotionNodes.size(); i++) {

							Node node = emotionNodes.get(i);
							if (node instanceof Element) {
								Element partElement = (Element) node;
                                                                System.out.println("Strategic Event: " + partElement.getAttributeValue("emotion"));
                                                                ssg.setLayer3Text(partElement.getAttributeValue("emotion"));
                                                        } 
                                                        
						}
					}
				};
			}
			am.addListener(mimircyEventAdapter);
			am.addListener(schematicEventAdapter);
			am.addListener(strategicEventAdapter);
			isListening = true;
		}
	}
        
        public ActiveMemory getMemory (){
            return am;
        }
        
        public boolean isListening() {
		return isListening;
	}

    void stopListening() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    }


    

