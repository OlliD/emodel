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
import net.sf.xcf.InitializeException;
import net.sf.xcf.memory.MemoryException;
import net.sf.xcf.naming.NameNotFoundException;

/**
 *
 * @author odamm
 */



public class Controller {
    private Robot r;
        HeadPositions hp;

        
        public Controller() throws MemoryException, InitializeException, NameNotFoundException{
            
        StrategySelectorGui ssg = new StrategySelectorGui();
        ssg.setVisible(true);
        
        System.err.println( "StrategySelector startet!" );
        MemoryConnector mc = new MemoryConnector(ssg);
        
        r = new Robot();
        hp = new HeadPositions();
        
        HCGui eg = new HCGui(r, hp);
        eg.setVisible(true);
        mc.startListening();
        
        }

        

    public void worker() throws InterruptedException {
        while(true){
            Thread.sleep(1000);
            
    }
}
}
