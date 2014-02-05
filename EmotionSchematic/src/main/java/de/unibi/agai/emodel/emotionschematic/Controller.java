/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.agai.emodel.emotionschematic;

import de.unibi.agai.emodel.emotionschematic.gui.SchematicGui;
import de.unibi.agai.emodel.emotionschematic.xcf.MemoryConnector;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.xcf.InitializeException;
import net.sf.xcf.memory.MemoryException;
import net.sf.xcf.naming.NameNotFoundException;

/**
 *
 * @author odamm
 */
public class Controller {

    private SchematicGui gui;
    private boolean bodyDetector = false;
    private MemoryConnector mc;
    private SchemataSelector ss;
    private boolean run = false;
    private ArrayList<Person> persons;

    public Controller() throws InitializeException, NameNotFoundException {
        gui = new SchematicGui();
        addActionListener();
        ArrayList<Person> persons = new ArrayList<Person>();
        mc = new MemoryConnector();
        //ss = new SchemataSelector();
        gui.setVisible(true);
    }

    private void addActionListener() {
        gui.addButtonStartListener(new ButtonStartListener());
        gui.addButtonStopListener(new ButtonStopListener());
    }

    private void worker() throws MemoryException {
        if (bodyDetector) {
            mc.startListening("/PERCEPTS");
            new Thread() {
                @Override
                public void run() {
                    while (run){
                        try {
                            Thread.sleep(1000);
                            
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                }
            }.start();

        } else {
            System.out.println("nothing to do");
        }

    }

    class ButtonStartListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            run = true;
            bodyDetector = gui.getjCheckBox2();
            System.out.println(bodyDetector);
            try {
                worker();
            } catch (MemoryException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    class ButtonStopListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
          run = false;
        }

    }
}
