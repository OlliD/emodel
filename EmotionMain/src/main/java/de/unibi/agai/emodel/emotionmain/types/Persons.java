/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.agai.emodel.emotionmain.types;

import java.util.ArrayList;

/**
 *
 * @author odamm
 */
public class Persons {

    private ArrayList<Person> persons;

    private Person player;
    private Person other;
    private boolean secondPersonDetected = false;
    private boolean playerDetected = false;

    public Persons() {
        persons = new ArrayList<Person>();

    }

    public double distance(Person p, Person p2) {
        long dx = p.getX() - p2.getX();
        long dy = p.getY() - p2.getY();
        long dz = p.getZ() - p2.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /*
     Füge eine neue Person in die Personenliste. Kontrolliere zunächst ob die
     Person schon vorhanden ist (ID / Distance) und aktualisere sie. Sonst füge
     sie hinzu
     */
    public void addNewPerson(Person person) {
        boolean add = false;
        if (persons.size() > 0) {
            for (int i = 0; i < persons.size(); i++) {
                System.out.println("Checking ID");
                if (persons.get(i).getId() != person.getId()) {
                    add = true;
                } else if (persons.get(i).getId() == person.getId()) {
                    persons.get(i).setX(person.getX());
                    persons.get(i).setY(person.getY());
                    persons.get(i).setZ(person.getZ());
                    System.out.println("Person updated. New X is: " + persons.get(i).getY());
                    add = false;
                    break;
                } else if (distance(person, persons.get(i)) > 10) {
                    System.out.println("Distance is: " + distance(person, persons.get(i)) + " - Personen added");
                }
            }
            if (add) {
                persons.add(person);
                if (distance(person, player) > 600) {
                    secondPersonDetected = true;
                    System.out.println("Found second person at: ");
                    other=person;
                }
                add = false;
            }
        }
        if (persons.size() == 0) {
            persons.add(person);
            player = person;
            playerDetected = true;
            System.out.println("first Person added");
        }

    }

    public Person getPlayer() {
        return player;
    }
    
    public Person getOther(){
        return other;
    }
    public boolean playerDetected(){
        return playerDetected;
    }
    
    public boolean otherPerson(){
        return secondPersonDetected;
    }

    public void printList() {
        for (int i = 0; i < persons.size(); i++) {
            System.out.println("ID: " + persons.get(i).getId());
            System.out.println("XPos: " + persons.get(i).getX());
            System.out.println("YPos: " + persons.get(i).getY());
            System.out.println("ZPos: " + persons.get(i).getZ());
        }
    }

    public int getSize() {
        return persons.size();
    }

}
