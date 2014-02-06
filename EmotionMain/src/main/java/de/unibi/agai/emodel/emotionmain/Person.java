/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.agai.emodel.emotionmain;

/**
 *
 * @author odamm
 */
public class Person {

    int id = 0;
    int x = 0;
    int y = 0;
    int z = 0;
    long detected = 0;
    long updated = 0;

    public Person(int id, int x, int y, int z) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public long getDetected() {
        return detected;
    }

    public void setDetected(long detected) {
        this.detected = detected;
    }

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void print() {
        System.out.println(" ");
        System.out.println("#######");
        System.out.println("ID: " + id);
        System.out.println("X: " + x);
        System.out.println("Y: " + y);
        System.out.println("Z: " + z);
        System.out.println("#######");
        System.out.println(" ");
        
    }
}
