/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.agai.emodel.emotionstrategic;

import de.unibi.agai.emodel.emotionstrategic.gui.Gui;
import de.unibi.agai.emodel.emotionstrategic.types.GameState;
import de.unibi.agai.emodel.emotionstrategic.xcf.MemoryConnector;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.xcf.InitializeException;
import net.sf.xcf.memory.MemoryException;
import net.sf.xcf.naming.NameNotFoundException;

/**
 *
 * @author odamm
 */
public class ControllerStrategic {

    private Gui gui;
    private MemoryConnector mc;
    private boolean run = false;
    private GameState gm;
    private int threshold = 3;

    public ControllerStrategic() throws InitializeException, NameNotFoundException {
        mc = new MemoryConnector();
        gui = new Gui();
        gui.setVisible(true);
        addActionListener();
    }

    private void addActionListener() {
        this.gui.addCheckBoxStartListening(new CheckBoxStartListening());
        this.gui.addButtonStartListening(new ButtonStartListener());
        this.gui.addButtonStopListening(new ButtonStopListener());
    }

    private void worker() {
        new Thread() {
            @Override
            public void run() {
                while (run) {
                    try {
                        if (mc.gameStateReady()) {
                            gm = mc.getGameState();
                            if (gm.getHuman() > threshold) {
                                try {
                                    System.out.println("get " + gm.getHuman() + " answer: TriggerEmotion");
                                    mc.continueDialog("TriggerEmotion", "Okay, weiter geht es.");
                                } catch (MemoryException ex) {
                                    Logger.getLogger(ControllerStrategic.class.getName()).log(Level.SEVERE, null, ex);
                                }

                            } else {
                                try {
                                    System.out.println("get " + gm.getHuman() + " answer: OtherQuestion");
                                    mc.continueDialog("OtherQuestion", "Bist du sicher das du das fragen mmoechtest?");
                                           
                                } catch (MemoryException ex) {
                                    Logger.getLogger(ControllerStrategic.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                        }
                        Thread.sleep(300);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ControllerStrategic.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }

        }.start();
    }

    class CheckBoxStartListening implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (gui.getCheckBoxStartListeningState()) {
                try {
                    mc.startListening();
                } catch (MemoryException ex) {
                    Logger.getLogger(ControllerStrategic.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (!gui.getCheckBoxStartListeningState()) {
                try {
                    mc.stopListening();
                } catch (MemoryException ex) {
                    Logger.getLogger(ControllerStrategic.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    class ButtonStartListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            run = true;
            worker();

        }

    }

    class ButtonStopListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            run = false;
        }

    }

}
