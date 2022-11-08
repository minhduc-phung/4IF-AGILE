/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author bbbbb
 */
public class Tour {
    private HashMap<Long, List<Segment>> tour = new HashMap<>();
    public Tour() {
        
    }
    
    public void addTour(Long idDeliveryPoint, List<Segment> listSeg) {
        tour.put(idDeliveryPoint, listSeg);
    }
    
    public List<Segment> getListSegment(Long idIntersection) {
        return tour.get(idIntersection);
    }
    
    public void emptyTour() {
        tour.clear();
    }

    @Override
    public String toString() {
        return "Tour{" + "tour=" + tour + '}';
    }

    public HashMap<Long, List<Segment>> getTour() {
        return tour;
    }
    
    
}
