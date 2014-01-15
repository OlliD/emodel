/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.unibi.agai.emodel.emotionmimicry.xcf;

import com.sun.media.jai.codec.MemoryCacheSeekableStream;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import net.sf.xcf.ActiveMemory;
import net.sf.xcf.InitializeException;
import net.sf.xcf.XcfException;
import net.sf.xcf.memory.MemoryException;
import net.sf.xcf.naming.NameNotFoundException;
       	

/**
 *
 * @author odamm
 */
public class MemoryListener implements Runnable {

    private ActiveMemory am;
    private MemoryConnector mc;
                
    
    public MemoryListener(MemoryConnector mc){
        this.mc = mc;
    }
    
    private void startXcfListener() throws InitializeException, NameNotFoundException, MemoryException {
		
					if (mc == null) {
                                                System.err.println("creating new Connector");
						mc = new MemoryConnector();
					}

                                        mc.startListening();
                                        
					while (mc.isListening()) {
                                            System.out.println("alive ");
					}
			}

	
	private void stopXcfListener() throws MemoryException {
		
		if (mc != null) {
                    mc.stopListening();
		} else {
			System.err.println("Stop without being started?!");
		}
	}

    public void run() {
        try {
            this.startXcfListener();
        } catch (InitializeException ex) {
            Logger.getLogger(MemoryListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NameNotFoundException ex) {
            Logger.getLogger(MemoryListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MemoryException ex) {
            Logger.getLogger(MemoryListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
