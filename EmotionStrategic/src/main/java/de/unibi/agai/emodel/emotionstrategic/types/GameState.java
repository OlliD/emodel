/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.unibi.agai.emodel.emotionstrategic.types;

/**
 *
 * @author odamm
 */
public class GameState {
    private int human;
    private int flobi;

    public GameState(int human, int flobi) {
        this.human = human;
        this.flobi = flobi;
    }

    public int getHuman() {
        return human;
    }

    public void setHuman(int human) {
        this.human = human;
    }

    public int getFlobi() {
        return flobi;
    }

    public void setFlobi(int flobi) {
        this.flobi = flobi;
    }
    
    
}
