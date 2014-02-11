/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.agai.emodel.emotionmain.types;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author odamm
 */
public class Face {

    //private String currentEmotion;
    private String lastEmotion;
    private long timeStamp;
    private int viewCount;
    private int lastId;
    private int currentId;
    private Map<String, Float> emotions;

    
    public Face(int currentId) {
        this.currentId = currentId;
        this.timeStamp = 0;
        emotions = new HashMap<String, Float>();
        emotions.put("Happy", 0f);
        emotions.put("Angry", 0f);
        emotions.put("Sad", 0f);
        emotions.put("Surprised", 0f);

    }

    public long getTimpStamp() {
        return timeStamp;
    }
    
    public void printEmotionList(){
        Map<String, Float> sortedEmotions = new HashMap<String, Float>();
        sortedEmotions = sortByValue(emotions);
        System.out.println(sortedEmotions.keySet().toArray()[3]);
        System.out.println(sortedEmotions.values().toArray()[3]);
        for (Map.Entry<String, Float> entry : sortedEmotions.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }
    
    public float getEmotionByName(String name){
        
        return emotions.get(name);
    }
    
    public String getMostLikelyEmotion(){
        Map<String, Float> sortedEmotions = sortByValue(emotions);
        //System.out.println(sortedEmotions.keySet().toArray()[3]);
        //System.out.println(sortedEmotions.values().toArray()[3]);
        
        return (String) sortedEmotions.keySet().toArray()[3];
    }

    public void setTimpStamp(long timpStamp) {
        this.timeStamp = timpStamp;
    }

    public Face getFace(){
        return new Face(999);
    }
    
    public float getReliability(String emotion){
        return emotions.get(emotion);
    }
    
    public String getLastEmotion() {
        return lastEmotion;
    }
/*    
    public String getcurrentEmotion() {
        return currentEmotion;
    }
*/
    public int getViewCount() {
        return viewCount;
    }

    public int getLastId() {
        return lastId;
    }

    public int getCurrentId() {
        return currentId;
    }
/*
    public void setCurrentEmotion(String currentEmotion) {
        this.currentEmotion = currentEmotion;
    }
*/
    public void setLastEmotion(String lastEmotion) {
        this.lastEmotion = lastEmotion;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public void setLastId(int lastId) {
        this.lastId = lastId;
    }

    public void setCurrentId(int currentId) {
        this.currentId = currentId;
    }

    public Map<String, Float> getEmotions() {
        return emotions;
    }

    public void setEmotions(Map<String, Float> emotions){
        this.emotions = emotions;
    }
    
    public void addEmotion(String emotion, float value) {
        emotions.put(emotion, value);
    }
    public void printFace(){
        System.out.println("##### FACES #####");
        System.out.println("Current ID: " + this.getCurrentId());
        System.out.println("Last ID: " + this.getLastId());
        System.out.println("ViewCount: " + this.getViewCount());
        for (Map.Entry<String, Float> entry : emotions.entrySet()) {
            System.out.println("Emotion " + entry.getKey() + " Value: " +entry.getValue());
        }
        System.out.println("TimeStamp: " + this.timeStamp );
        System.out.println("##### ##### #####");

    }
    
    private Map sortByValue(Map map) {
     List list = new LinkedList(map.entrySet());
     Collections.sort(list, new Comparator() {
          public int compare(Object o1, Object o2) {
               return ((Comparable) ((Map.Entry) (o1)).getValue())
              .compareTo(((Map.Entry) (o2)).getValue());
          }
     });

    Map result = new LinkedHashMap();
    for (Iterator it = list.iterator(); it.hasNext();) {
        Map.Entry entry = (Map.Entry)it.next();
        result.put(entry.getKey(), entry.getValue());
    }
    return result;
} 
}
