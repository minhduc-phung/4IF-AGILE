/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *This class defines a segment (who's characterised by an origin intersection, a destination intersection, a name and a length in meters, it does not have any other intersection in it)>
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

    public Double getLength() {
        return length;
    }

    public Intersection getOrigin() {
        return origin;
    }

    public Intersection getDestination() {
        return destination;
    }

    public String getName() {
        return name;
    }
}