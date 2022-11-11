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
    private HashMap<Long, List<Segment>> tourRoute = new HashMap<>();
    public Tour() {
        
    }
    
    public void addTourRoute(Long idDeliveryPoint, List<Segment> listSeg) {
        tourRoute.put(idDeliveryPoint, listSeg);
    }
    
    public List<Segment> getListSegment(Long idIntersection) {
        return tourRoute.get(idIntersection);
    }
    
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
