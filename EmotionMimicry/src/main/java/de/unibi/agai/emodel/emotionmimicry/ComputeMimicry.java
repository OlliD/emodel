/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.agai.emodel.emotionmimicry;

import de.unibi.agai.emodel.emotionmimicry.gui.Gui;
import de.unibi.agai.emodel.emotionmimicry.xcf.MemoryConnector;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.xcf.InitializeException;
import net.sf.xcf.memory.MemoryException;
import net.sf.xcf.naming.NameNotFoundException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author odamm
 */
public class ComputeMimicry {

    private MemoryConnector mc;
    private Gui gui;
    private String inputSelector = "";
    private int reliability = 100;
    private boolean rmimic = false;
    private boolean shore = false;
    private boolean emotionmain = false;

    ComputeMimicry() throws InitializeException, NameNotFoundException {

        mc = new MemoryConnector();
        gui = new Gui();
        addActionListener();
        gui.setVisible(true);
    }

    private void addActionListener() {
        gui.setStartCheckBoxListener(new startEnabledListener());
    }

    private void run() throws InterruptedException {
        new Thread() {
            @Override
            public void run() {
                while (rmimic) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ComputeMimicry.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (mc.eventTriggered()) {
                        try {
                            //System.out.println("get Event " + mc.getEvent());
                            pushReaction(mc.getEvent());
                        } catch (MemoryException ex) {
                            Logger.getLogger(ComputeMimicry.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        System.out.println("Nothing");
                    }
                }

                while (shore) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ComputeMimicry.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (mc.eventTriggered()) {
                        try {
                            pushReaction(mc.getEvent());
                        } catch (MemoryException ex) {
                            Logger.getLogger(ComputeMimicry.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }

                while (emotionmain) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ComputeMimicry.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (mc.eventTriggered()) {
                        try {
                            pushReaction(mc.getEvent());
                        } catch (MemoryException ex) {
                            Logger.getLogger(ComputeMimicry.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else if (!mc.eventTriggered()) {
                        String[] ret = new String[2];
                        ret[0] = "none";
                        ret[1] = "0f";
                        try {
                            pushReaction(ret);
                        } catch (MemoryException ex) {
                            Logger.getLogger(ComputeMimicry.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }.start();
    }

    private void pushReaction(String[] event) throws MemoryException {
        if (Double.parseDouble(event[1]) > reliability) {
            System.out.println("sending Emotion to Mimic");
            if (event[0].equals("Happy")){
                System.out.println("more happy");
            }
            mc.insertToMemory("Mimicry", event[0]);
        }
    }

    class startEnabledListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            System.out.println("ACTION" + gui.getCheckBoxState());
            if (gui.getCheckBoxState()) {
                try {

                    rmimic = gui.getRMimicButton();
                    if (rmimic) {
                        mc.startListening("rmimic");
                    }

                    shore = gui.getShoreButton();
                    if (shore) {
                        mc.startListening("shore");
                    }

                    emotionmain = gui.getEmotionMainButton();
                    if (emotionmain) {
                        mc.startListening("Facial");
                    }

                    reliability = gui.getReliability();
                    try {
                        run();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ComputeMimicry.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (MemoryException ex) {
                    Logger.getLogger(ComputeMimicry.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (!gui.getCheckBoxState()) {
                try {
                    rmimic = false;
                    shore = false;
                    emotionmain = false;
                    mc.stopListening();
                } catch (MemoryException ex) {
                    Logger.getLogger(ComputeMimicry.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
