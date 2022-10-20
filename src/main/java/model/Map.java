/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.List;

/**
 *
 * @author nmngo
 */
public class Map {
    private List<Intersection> listIntersection;
    private List<Segment> listSegment;

    public Map(List<Intersection> listIntersection, List<Segment> listSegment) {
        this.listIntersection = listIntersection;
        this.listSegment = listSegment;
    }
    
}
