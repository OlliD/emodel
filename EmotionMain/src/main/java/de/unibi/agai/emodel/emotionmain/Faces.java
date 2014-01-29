/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.agai.emodel.emotionmain;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author odamm
 */
public class Faces {

    private String currentEmotion;
    private String lastEmotion;
    private int viewCount;
    private int lastId;
    private int currentId;
    private Map<String, Float> emotions;

    public Faces(String currentEmotion, int viewCount, int currentId) {
        this.currentEmotion = currentEmotion;
        this.viewCount = viewCount;
        this.currentId = currentId;
        emotions = new HashMap<String, Float>();
        emotions = new HashMap<String, Float>();
        emotions.put("Happy", 0f);
        emotions.put("Angry", 0f);
        emotions.put("Sad", 0f);
        emotions.put("Surprised", 0f);

    }

    public String getLastEmotion() {
        return lastEmotion;
    }
    
    public String getcurrentEmotion() {
        return currentEmotion;
    }

    public int getViewCount() {
        return viewCount;
    }

    public int getLastId() {
        return lastId;
    }

    public int getCurrentId() {
        return currentId;
    }

    public void setCurrentEmotion(String currentEmotion) {
        this.currentEmotion = currentEmotion;
    }

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

    public void addEmotion(String emotion, float value) {
        emotions.put(emotion, value);
    }
    public void printFace(){
        System.out.println("Current ID: " + this.getCurrentId());
        System.out.println("Current ID: " + this.getLastId());
        System.out.println("Current ID: " + this.getViewCount());
        for (Map.Entry<String, Float> entry : emotions.entrySet()) {
            System.out.println("Emotion " + entry.getKey() + "Value: " +entry.getValue());
        }
    }
}
