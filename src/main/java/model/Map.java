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
     * The list of intersections in this map
     * @see Intersection
     */
    private HashMap<Long, Intersection> listIntersection;
    /**
     * The list of segments on this map
     * @see Segment
     */
    private List<Segment> listSegment;
    /**
     * The warehouse: a special intersection where all parcels are stored, and each courier starts delivering parcels from here at 8 o'clock every morning and returns to the warehouse after delivering all the parcels every day.
     */
    private Intersection warehouse;
    private String mapName;

    public Map() {
    
    }

    /**
     * Create a map with the name of the XML file defining it, the list of intersections and the list of segments and the warehouse, all of which are defined in the XML file
     * @param mapName the name of the XML file defining the map
     * @param listIntersection the list of intersections
     * @param listSegment the list of segments
     * @param warehouse the warehouse
     */
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

    /**
     * Get the latitude of the lowest intersection
     */
    public Double getMinLatitude() {
        Double minLat = Double.MAX_VALUE;
        for (Intersection i : listIntersection.values()) {
            if(i.getLatitude() < minLat) {
                minLat = i.getLatitude();
            }
        }
        return minLat;
    }

    /**
     * Get the longitude of the left-most intersection
     */
    public Double getMinLongitude() {
        Double minLong = Double.MAX_VALUE;
        for (Intersection i : listIntersection.values()) {
            if(i.getLongitude() < minLong) {
                minLong = i.getLongitude();
            }
        }
        return minLong;
    }

    /**
     * Get the latitude of the highest intersection
     */
    public Double getMaxLatitude() {
        Double maxLat = Double.MIN_VALUE;
        for (Intersection i : listIntersection.values()) {
            if(i.getLatitude() > maxLat) {
                maxLat = i.getLatitude();
            }
        }
        return maxLat;
    }

    /**
     * Get the latitude of the right-most intersection
     */
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
