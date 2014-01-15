package de.unibi.agai.emodel.emotionschematic;

import de.unibi.agai.emodel.emotionschematic.gui.Gui;
import de.unibi.agai.emodel.emotionschematic.xcf.MemoryConnector;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import net.sf.xcf.InitializeException;
import net.sf.xcf.memory.MemoryException;
import net.sf.xcf.naming.NameNotFoundException;
import org.xml.sax.SAXException;

/**
 * Hello world!
 *
 */
public class Main 
{
    public static void main( String[] args ) throws InitializeException, NameNotFoundException, MemoryException, InterruptedException, ParserConfigurationException, SAXException, IOException
    {
        MemoryConnector mc = new MemoryConnector();
        Gui gui = new Gui(mc);
        gui.setVisible(true);
        SchemataSelector ss = new SchemataSelector(mc);

    
    }
}
