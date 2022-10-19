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
    private Long origin;
    private Long destination;
    private Double length;
    private String name;

    public Segment(Long origin, Long destination, Double length, String name) {
        this.origin = origin;
        this.destination = destination;
        this.length = length;
        this.name = name;
    }
    
}
