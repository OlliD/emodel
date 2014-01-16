package de.unibi.agai.emodel.emotionmimicry;


import de.unibi.agai.emodel.emotionmimicry.gui.Gui;
import de.unibi.agai.emodel.emotionmimicry.xcf.MemoryConnector;
import net.sf.xcf.XcfException;


/**
 * Hello world!
 *
 */
public class Main 
{

 
    
    public static void main( String[] args ) throws XcfException, InterruptedException
    {
        MemoryConnector mc = new MemoryConnector();
        ComputeMimicry cm = new ComputeMimicry(mc);
        Gui gui = new Gui(mc, cm);
        gui.setVisible(true);
        }
    }
    

    
