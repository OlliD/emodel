/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.agai.emodel.emotionschematic;

import de.unibi.agai.emodel.emotionschematic.gui.SchematicGui;
import de.unibi.agai.emodel.emotionschematic.xcf.MemoryConnector;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
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
    private Persons persons;
    private Person old;
    private String[] p;
    private int interruptCounter = 0;
    private Map<Integer, String> schematic_map;

    public Controller() throws InitializeException, NameNotFoundException {
        gui = new SchematicGui();
        addActionListener();
        persons = new Persons();
        mc = new MemoryConnector();
        p = new String[4];

        schematic_map = new HashMap<Integer, String>();
        schematic_map.put(1, "Hallo, wir spielen gerade eine Partie wer bin ich");
        schematic_map.put(2, "Bitte lass uns weiter spielen");
        schematic_map.put(3, "Jetzt geh bitte und lass uns weiter spielen");

        gui.setVisible(true);
    }

    private void addActionListener() {
        gui.addButtonStartListener(new ButtonStartListener());
        gui.addButtonStopListener(new ButtonStopListener());
        gui.addButtonDialogContinueListener(new ButtonDialogContinueListener());
        gui.addButtonDialogInterruptListener(new ButtonDialogInterruptListener());
    }

    private void worker() throws MemoryException {
        if (bodyDetector) {
            mc.startListening("Emotion");
            new Thread() {
                @Override
                public void run() {
                    while (run) {
                        try {
                            if (mc.personReady()) {
                                p = mc.getCoordinates();
                                if (p[3].equals("true")) {
                                    gui.setPlayer(p[0], p[1], p[2], p[3]);
                                } else if (p[3].equals("false")) {
                                    gui.setOther(p[0], p[1], p[2], p[3]);
                                }

                                System.out.println(p[0] + " " + p[1] + " " + p[2] + " " + p[3]);
                            }
                            if (p[0] != null && p[1] != null && p[2] != null && p[3] != null) {
                                mc.insertToMemory("Schematic", p); // TODO: Refactor to PERSON!! 
                            }
                            Thread.sleep(2000);

                        } catch (InterruptedException ex) {
                            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (MemoryException ex) {
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

    class ButtonDialogInterruptListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            try {
                mc.interuptDialog("interrupt");
            } catch (MemoryException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    class ButtonDialogContinueListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            try {
                //mc.say("Bitte lass uns weiter spielen", String.valueOf(0.0f) , String.valueOf(1.0f), String.valueOf(0.4f));
                mc.interuptDialog("interruptdone");
            } catch (MemoryException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
