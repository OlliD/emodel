/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.unibi.agai.emodel.emotionmain;

import de.unibi.agai.emodel.emotionmain.xcf.MemoryConnector;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
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
    private MemoryConnector contextConnector;
    private MemoryConnector faceConnector;
    private Map<String, Float> emotions;
    private Float threshold = 50f; 

         
    public Controller() throws InitializeException, NameNotFoundException, InterruptedException, MemoryException{
        XcfManager xm = XcfManager.createXcfManager();
        ActiveMemory am_ST = xm.createActiveMemory("ShortTerm");
        ActiveMemory am_V = xm.createActiveMemory("vision");

        contextConnector = new MemoryConnector("context", am_V);
        speechConnector = new MemoryConnector("speech", am_ST);
        faceConnector = new MemoryConnector("OBJECTS", am_V);
        emotions = new HashMap<String, Float>();
        this.worker();
        }
        

    private void worker() throws InterruptedException, MemoryException{
        faceConnector.startListening();

        
        while(true){
        
            Thread.sleep(2000);
            emotions = faceConnector.getEmotionMap();
            //Looking for the emotion map and receive a map of emotions (happy, sad, surprised, angry) with value for reliability
            //when one value is over the threshold the label will be inserted into the memory
            for (Map.Entry<String, Float> entry : emotions.entrySet()) {
                if (entry.getValue()>threshold){
                    System.out.println(entry.getKey() + " = " + entry.getValue());
                    speechConnector.insertToMemory("mimicry", entry.getKey(), entry.getValue().toString());
                    emotions.put(entry.getKey(), 0f);

                }
            }
            }
        
        }


}   
