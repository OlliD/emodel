package de.unibi.agai.emodel.emotionmain;

import de.unibi.agai.eb.BusException;
import de.unibi.agai.emodel.emotionmain.xcf.MemoryConnector;
import de.unibi.agai.emodel.gui.EmotionMainGui;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.xcf.ActiveMemory;
import net.sf.xcf.InitializeException;
import net.sf.xcf.XcfManager;
import net.sf.xcf.memory.MemoryException;
import net.sf.xcf.naming.NameNotFoundException;



public class Main 
{
    
    public static void main( String[] args ) throws InitializeException, NameNotFoundException, MemoryException, InterruptedException, BusException
        {
        ControllerEmotionMain c = new ControllerEmotionMain();

    }
}
