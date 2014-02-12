package de.unibi.agai.emodel.emotionstrategyselector;

import de.unibi.agai.dapi.pack.PackerNotFoundException;
import de.unibi.agai.eb.BusException;
import de.unibi.agai.emodel.emotionstrategyselector.gui.StrategySelectorGui;
import de.unibi.agai.emodel.emotionstrategyselector.robotconnector.HCGui;
import de.unibi.agai.emodel.emotionstrategyselector.xcf.MemoryConnector;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
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
    public static void main( String[] args ) throws XcfException, InterruptedException, MemoryException, InitializeException, NameNotFoundException, IOException, ExecutionException, TimeoutException, BusException, PackerNotFoundException
    {
        Controller c = new Controller();
        //c.worker();
    }
}
