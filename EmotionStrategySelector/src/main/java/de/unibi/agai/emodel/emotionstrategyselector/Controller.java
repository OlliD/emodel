/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.unibi.agai.emodel.emotionstrategyselector;

import de.unibi.agai.emodel.emotionstrategyselector.gui.StrategySelectorGui;
import de.unibi.agai.emodel.emotionstrategyselector.robotconnector.HCGui;
import de.unibi.agai.emodel.emotionstrategyselector.robotconnector.HeadPositions;
import de.unibi.agai.emodel.emotionstrategyselector.robotconnector.Robot;
import de.unibi.agai.emodel.emotionstrategyselector.xcf.MemoryConnector;
import de.unibi.flobi.Actuators;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.xcf.InitializeException;
import net.sf.xcf.memory.MemoryException;
import net.sf.xcf.naming.NameNotFoundException;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;

/**
 *
 * @author odamm
 */



public class Controller {
        private Robot r;
        private HeadPositions hp;
        MemoryConnector mc;
        private String strategicEmotion;
        private String mimircyEmotion;
        private String schematicEmotion;
        private StrategySelectorGui ssg;
        public Controller() throws MemoryException, InitializeException, NameNotFoundException, IOException, ExecutionException, InterruptedException, TimeoutException{
            
        ssg = new StrategySelectorGui();
        ssg.setVisible(true);
        addListener();
        
        System.err.println( "StrategySelector startet!" );
        mc = new MemoryConnector(ssg);
        
        r = new Robot();
        hp = new HeadPositions();
        
        List<String> poses = new ArrayList();
        for (String s : hp.getPositions().keySet()) {
            poses.add(s);
        }
        Collections.sort(poses);
        
        //HCGui eg = new HCGui(r, hp);
        //eg.setVisible(true);
        mc.startListening();
        worker();
        }

        

    public void worker() throws InterruptedException, IOException, ExecutionException, TimeoutException {
        String emotion ="";
        int cooldown = 0;
        while(true){
                    emotion = mc.expressEmotion();
                    if (emotion!=""){
                        sendEmotion(emotion);
                    }
                    cooldown++;
                    Thread.sleep(1000);
                    if (cooldown == 5){
                        cooldown = 0;
                        r.executeMovement(hp.getPosition("neutral").getActuatorList(),30, 150);
                    }
        }
        
   
        

}
    

    
    private void sendEmotion (String emotion) throws IOException, ExecutionException, TimeoutException, InterruptedException{
        r.executeMovement(hp.getPosition(emotion.toLowerCase()).getActuatorList(),30, 150);

    }
    
    
        private void addListener(){
        this.ssg.setLayer1CheckboxListener(new Layer1CheckboxListener());
        this.ssg.setLayer2CheckboxListener(new Layer2CheckboxListener());
        this.ssg.setLayer3CheckboxListener(new Layer3CheckboxListener());
    }

    
    class Layer1CheckboxListener implements ActionListener{

        public void actionPerformed(ActionEvent e) {

            boolean wert = ssg.getOnOffCheckBoxLayer1();
            System.out.println("Set Layer 1? " + ssg.getOnOffCheckBoxLayer1());
            try {
                mc.startListeningLayer1(wert);
            } catch (MemoryException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    
    class Layer2CheckboxListener implements ActionListener{

        public void actionPerformed(ActionEvent e) {

            boolean wert = ssg.getOnOffCheckBoxLayer2();
            System.out.println("Set Layer 2? " + ssg.getOnOffCheckBoxLayer2());
            try {
                mc.startListeningLayer2(wert);
            } catch (MemoryException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }
    
    class Layer3CheckboxListener implements ActionListener{

        public void actionPerformed(ActionEvent e) {

            boolean wert = ssg.getOnOffCheckBoxLayer3();
            System.out.println("Set Layer 3? " + ssg.getOnOffCheckBoxLayer3());
            try {
                mc.startListeningLayer3(wert);
            } catch (MemoryException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }
}