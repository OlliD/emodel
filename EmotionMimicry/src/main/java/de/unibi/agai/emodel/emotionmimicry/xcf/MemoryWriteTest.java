
package de.unibi.agai.emodel.emotionmimicry.xcf;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.xcf.ActiveMemory;
import net.sf.xcf.InitializeException;
import net.sf.xcf.XcfManager;
import net.sf.xcf.event.MemoryEvent;
import net.sf.xcf.event.MemoryEventAdapter;
import net.sf.xcf.fts.MemorySource;
import net.sf.xcf.fts.engine.EngineThread;
import net.sf.xcf.fts.engine.Engines;
import net.sf.xcf.fts.engine.Graph;
import net.sf.xcf.fts.nodes.sink.SinkAdapter;
import net.sf.xcf.memory.MemoryAction;
import net.sf.xcf.memory.MemoryException;
import net.sf.xcf.naming.NameNotFoundException;
import net.sf.xcf.transport.XOPData;
import net.sf.xcf.xml.XPath;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;

/**
 *
 * @author pholthau
 */
public class MemoryWriteTest implements Runnable {
   
    public XcfManager xm = null;
    private ActiveMemory am = null;

    public MemoryWriteTest(XcfManager xm, ActiveMemory am) throws InitializeException, NameNotFoundException, MemoryException{
        this.xm = xm;
        this.am = am;

    }


    public void testMemoryVanilla() throws Exception {
        System.out.println("memory created");
        while(true){
        Element root = new Element("SpeedTest");
            root.addAttribute(new Attribute("Hello", "World"));
            am.insert(new XOPData(new Document(root)));
            System.out.println("inserted in " + am.getName());
       Thread.sleep(1500);
        }
       
   }

    public void run() {
        try {
            this.testMemoryVanilla();
        } catch (Exception ex) {
            Logger.getLogger(MemoryWriteTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
