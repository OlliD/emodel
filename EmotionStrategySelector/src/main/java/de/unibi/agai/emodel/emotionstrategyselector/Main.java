package de.unibi.agai.emodel.emotionstrategyselector;

import de.unibi.agai.dapi.pack.PackerNotFoundException;
import de.unibi.agai.eb.BusException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.xcf.InitializeException;
import net.sf.xcf.XcfException;
import net.sf.xcf.memory.MemoryException;
import net.sf.xcf.naming.NameNotFoundException;

/**
 * Hello world!
 *
 */
public class Main 
{
    public static void main( String[] args ) throws XcfException, InterruptedException, MemoryException, InitializeException, NameNotFoundException, IOException, ExecutionException, TimeoutException, BusException 
    {
        try {
            Controller c = new Controller();
            //c.worker();
        } catch (PackerNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
