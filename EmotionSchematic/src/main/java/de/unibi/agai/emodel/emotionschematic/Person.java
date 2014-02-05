/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.agai.emodel.emotionschematic;

/**
 *
 * @author odamm
 */
public class Person {

    int id = 0;
    long x = 0;
    long y = 0;
    long z = 0;
    long detected = 0;
    long updated = 0;

    public Person(int id, long x, long y, long z) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public long getX() {
        return x;
    }

    public void setX(long x) {
        this.x = x;
    }

    public long getY() {
        return y;
    }

    public void setY(long y) {
        this.y = y;
    }

    public long getZ() {
        return z;
    }

    public void setZ(long z) {
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
}
