/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author nmngo
 */
public class Segment {
    private Intersection origin;
    private Intersection destination;
    private Double length;
    private String name;

    public Segment(Intersection origin, Intersection destination, Double length, String name) {
        this.origin = origin;
        this.destination = destination;
        this.length = length;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Segment{" + "origin=" + origin + ", destination=" + destination + ", length=" + length + ", name=" + name + '}';
    }

    
}
