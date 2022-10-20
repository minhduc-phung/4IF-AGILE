/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author nmngo
 */
public class Map {
    private HashMap<Long, Intersection> listIntersection;
    private List<Segment> listSegment;

    private Intersection warehouse;

    public Map(HashMap<Long, Intersection> listIntersection, List<Segment> listSegment, Intersection warehouse) {
        this.listIntersection = listIntersection;
        this.listSegment = listSegment;
        this.warehouse = warehouse;
    }

    public List<Segment> getListSegment() {
        return listSegment;
    }

    public HashMap<Long, Intersection> getListIntersection() {
        return listIntersection;
    }

    public Intersection getWarehouse() {
        return warehouse;
    }

    public Double[] getMinMaxCoordinates() {
        Double[] minMax = new Double[4];
        // first value, smallest Latitude
        minMax[0] = Double.MAX_VALUE;
        // second value, smallest Longitude
        minMax[1] = Double.MAX_VALUE;
        // third value, biggest Latitude
        minMax[2] = Double.MIN_VALUE;
        // fourth value, biggest Longitude
        minMax[3] = Double.MIN_VALUE;
        for (Intersection i : listIntersection.values()) {
            if(i.getLatitude() < minMax[0]) {
                minMax[0] = i.getLatitude();
            }
            if(i.getLatitude() > minMax[2]) {
                minMax[2] = i.getLatitude();
            }
            if(i.getLongitude() < minMax[1]) {
                minMax[1] = i.getLongitude();
            }
            if(i.getLongitude() > minMax[3]) {
                minMax[3] = i.getLongitude();
            }
        }
        return minMax;
    }


}
