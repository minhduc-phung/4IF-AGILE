/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.HashMap;
import java.util.List;

/**
 * This class represents a tour (sequence of paths for a courier who starts from the warehouse, to deliver all the parcels on that day and then go back to the warehouse at the minimum timestamp).
 */
public class Tour {
    /**
     * The tour route (all the delivery points and the segments between them)
     */
    private HashMap<Long, List<Segment>> tourRoute = new HashMap<>();
    public Tour() {
        
    }
    /**
     * this method adds a route to the tour (delivery point and its segments)
     * @param idDeliveryPoint the id of the delivery point to add
     * @param listSeg the list of segments relative to the delivery point wanted to add
     */
    public void addTourRoute(Long idDeliveryPoint, List<Segment> listSeg) {
        tourRoute.put(idDeliveryPoint, listSeg);
    }
    
    public List<Segment> getListSegment(Long idIntersection) {
        return tourRoute.get(idIntersection);
    }

    /**
     * this method clear this tour (make it empty by deleting all the delivery points and their segments)
     */
    public void emptyTourRoute() {
        tourRoute.clear();
    }

    @Override
    public String toString() {
        return "Tour{" + "tour=" + tourRoute + '}';
    }

    public HashMap<Long, List<Segment>> getTourRoute() {
        return tourRoute;
    }
    
    
}
