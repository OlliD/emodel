
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
public class MemorySpeedTest implements Runnable {
   
    public XcfManager xm = null;
    private ActiveMemory am = null;

    /**
     *
     * @param xm
     * @param am
     * @throws InitializeException
     * @throws NameNotFoundException
     * @throws MemoryException
     */
    public MemorySpeedTest(XcfManager xm, ActiveMemory am) throws InitializeException, NameNotFoundException, MemoryException{
        this.xm = xm;
        this.am = am;
    }



    public void testMemoryVanilla() throws Exception {
        System.out.println("memory created");
        
            System.out.println("Speed Test query");
            am.addListener(new MemoryEventAdapter(MemoryAction.ALL, new XPath("/*")){
            @Override
            public void handleEvent(MemoryEvent e) {
                System.out.println("Event");
                System.out.println(e.getData().getDocumentAsText().replaceAll("\n", "\t"));
            }
        });
            while(true) {
                Thread.sleep(1000);
            }

    }

    public void run() {
        try {
            this.testMemoryVanilla();
        } catch (Exception ex) {
            Logger.getLogger(MemorySpeedTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
