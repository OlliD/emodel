/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.unibi.agai.emodel.emotionschematic;

import de.unibi.agai.emodel.emotionschematic.xcf.MemoryConnector;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import net.sf.xcf.memory.MemoryException;
import net.sf.xcf.xml.DocumentBuilder;
import nu.xom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author odamm
 */
public class SchemataSelector {

    private MemoryConnector mc;
    private final boolean eventTrigger = false;
    private NodeList eventList;
    
    SchemataSelector(MemoryConnector mc) throws InterruptedException, ParserConfigurationException, SAXException, IOException {
        File fXmlFile = new File("/homes/odamm/develop/emodel//schemata.xml");
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	     javax.xml.parsers.DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	     org.w3c.dom.Document doc = dBuilder.parse(fXmlFile);
	doc.getDocumentElement().normalize();
	System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
	eventList = doc.getElementsByTagName("event");
	System.out.println("----------------------------");
this.mc = mc;
        this.checkForSchemata();
        
        
        
    }

    SchemataSelector() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
   
    private void checkForSchemata() throws InterruptedException{
        while (true){
            Thread.sleep(1000);
            if (mc.personReady()){
                //System.out.println("get Event " + mc.getEvent());
                pushReaction(mc.getCoordinates());
            }
            else
                System.out.println("nothing to do");
        }
           
    }
    
    private void pushReaction(String[] event){
	for (int temp = 0; temp < eventList.getLength(); temp++) {
 
		Node nNode = eventList.item(temp);
		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
 			Element eElement = (Element) nNode;
                        if (event.equals(eElement.getAttribute("id"))){
                            System.out.println(">>> " + eElement.getElementsByTagName("reaction").item(0).getTextContent());
                            }
                }
	}
    } 
    
    private void activateSchemata() throws MemoryException{
        
        
        String elementName = "", attributeKey = "", attributeValue ="";
        
        
        mc.insertToMemory(elementName, attributeKey, attributeValue);
        
        
    }

}
   