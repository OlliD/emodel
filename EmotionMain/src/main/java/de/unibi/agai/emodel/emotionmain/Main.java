package de.unibi.agai.emodel.emotionmain;

import de.unibi.agai.emodel.gui.MainGui;
import net.sf.xcf.ActiveMemory;
import net.sf.xcf.InitializeException;
import net.sf.xcf.XcfManager;
import net.sf.xcf.memory.MemoryException;
import net.sf.xcf.naming.NameNotFoundException;



public class Main 
{
    private XcfManager xm;
    private ActiveMemory am;
    
    public static void main( String[] args ) throws InitializeException, NameNotFoundException, MemoryException
        {
        MainGui gui = new MainGui();
        gui.setVisible(true);
        System.out.println( "Hello World!" );
    }
}
