package de.unibi.agai.emodel.emotionstrategyselector;

import de.unibi.agai.emodel.emotionstrategyselector.gui.StrategySelectorGui;
import de.unibi.agai.emodel.emotionstrategyselector.xcf.MemoryConnector;
import net.sf.xcf.InitializeException;
import net.sf.xcf.XcfException;
import net.sf.xcf.memory.MemoryException;
import net.sf.xcf.naming.NameNotFoundException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws XcfException, InterruptedException
    {
        StrategySelectorGui ssg = new StrategySelectorGui();
        ssg.setVisible(true);
        System.err.println( "StrategySelector startet!" );
        MemoryConnector mc = new MemoryConnector(ssg);
        mc.startListening();
        while(true){
            Thread.sleep(1000);
        }
    }
}
