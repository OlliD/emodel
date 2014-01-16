/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.unibi.agai.emodel.emotionmimicry;

import de.unibi.agai.emodel.emotionmimicry.xcf.MemoryConnector;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author odamm
 */
public class ComputeMimicry {

    MemoryConnector mc;
    ComputeMimicry(MemoryConnector mc) {
        this.mc = mc;
    }
    
    private void checkForShoreMimicry() throws InterruptedException{
        while (true){
            Thread.sleep(1000);
            if (mc.eventTriggered()){
                //System.out.println("get Event " + mc.getEvent());
                pushReaction(mc.getEvent());
            }
            else
                System.out.println("nothing to do");
        }
           
    }
    private void checkForRMimicry() throws InterruptedException{
        while (true){
            Thread.sleep(1000);
            if (mc.eventTriggered()){
                //System.out.println("get Event " + mc.getEvent());
                pushReaction(mc.getEvent());
            }
            else
                System.out.println("nothing to do");
        }
           
    }
        private void pushReaction(String event){

        } 
}
