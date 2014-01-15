/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.unibi.agai.emodel.emotionmain.xcf;

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
        
        public MemoryConnector(String xpath) throws InitializeException, NameNotFoundException {
            xm = XcfManager.createXcfManager();
            am = xm.createActiveMemory("ShortTerm");
            this.xpath = xpath;
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
								"/*");

						// Get the phoneme chain text
						for (int i = 0; i < emotionNodes.size(); i++) {

							Node node = emotionNodes.get(i);
							if (node instanceof Element) {
								Element partElement = (Element) node;
                                                                System.out.println(partElement.getAttributeValue("EModel"));
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


}



    

