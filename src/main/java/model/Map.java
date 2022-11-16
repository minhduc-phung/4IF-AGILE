/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.HashMap;
import java.util.List;
import observer.Observable;

/**
 *this class defines the map (graph loaded from an XML file containing a list of intersections and road segments)
 */
public class Map extends Observable {
    /**
     * this attribute represents the list of intersections in this map
     * @see Intersection
     */
    private HashMap<Long, Intersection> listIntersection;
    /**
     * this attribute represents the list of segments on this map
     * @see Segment
     */
    private List<Segment> listSegment;
    /***
     * This attribute represents the warehouse (a special intersection where all parcels are stored, and each courier starts delivering parcels from here at 8 o'clock every morning and returns to the warehouse after delivering all the parcels every day).
     */
    private Intersection warehouse;
    private String mapName;

    public Map() {
    
    }
    
    public Map(String mapName, HashMap<Long, Intersection> listIntersection, List<Segment> listSegment, Intersection warehouse) {
        this.listIntersection = listIntersection;
        this.listSegment = listSegment;
        this.warehouse = warehouse;
        this.mapName = mapName;
    }

    public List<Segment> getListSegment() {
        return listSegment;
    }
    
    public Segment getSegment(Long idOrigin, Long idDest) {
        for (Segment seg : this.listSegment) {
            if (seg.getOrigin().id.equals(idOrigin) && seg.getDestination().id.equals(idDest)) {
                return seg;
            }
        }
        return null;
    }

    public HashMap<Long, Intersection> getListIntersection() {
        return listIntersection;
    }
    
    public Intersection getIntersection(Long idIntersection){
        Intersection inter = listIntersection.get(idIntersection);
        return inter;
        
    }
    public Intersection getWarehouse() {
        return warehouse;
    }

    public String getMapName() {
        return mapName;
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

    public Double getMinLatitude() {
        Double minLat = Double.MAX_VALUE;
        for (Intersection i : listIntersection.values()) {
            if(i.getLatitude() < minLat) {
                minLat = i.getLatitude();
            }
        }
        return minLat;
    }

    public Double getMinLongitude() {
        Double minLong = Double.MAX_VALUE;
        for (Intersection i : listIntersection.values()) {
            if(i.getLongitude() < minLong) {
                minLong = i.getLongitude();
            }
        }
        return minLong;
    }

    public Double getMaxLatitude() {
        Double maxLat = Double.MIN_VALUE;
        for (Intersection i : listIntersection.values()) {
            if(i.getLatitude() > maxLat) {
                maxLat = i.getLatitude();
            }
        }
        return maxLat;
    }

    public Double getMaxLongitude() {
        Double maxLong = Double.MIN_VALUE;
        for (Intersection i : listIntersection.values()) {
            if(i.getLongitude() > maxLong) {
                maxLong = i.getLongitude();
            }
        }
        return maxLong;
    }

}
