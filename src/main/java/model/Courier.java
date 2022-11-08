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
public class Courier {
    private Long id;
    private String name;
    private List<DeliveryPoint> currentDeliveryPoints = new ArrayList<>();
    private Tour currentTour = new Tour();

    private ArrayList<Long> positionIntersection = new ArrayList<>();
    private HashMap<Long, HashMap<Long, Double>> shortestPathBetweenDPs = new HashMap<>();;
    private HashMap<Long, Tour> listSegmentBetweenDPs = new HashMap<>();
    
    public Courier(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }
    
    public List<DeliveryPoint> getCurrentDeliveryPoints() {
        return currentDeliveryPoints;
    }

    public String getName() {
        return name;
    }
    
    public void addCurrentTour(Long idDeliveryPoint, List<Segment> listSeg) {
        this.currentTour.addTour(idDeliveryPoint, listSeg);
    }

    public List<Segment> getListSegmentCurrentTour(Long idDeliveryPoint) {
        return currentTour.getListSegment(idDeliveryPoint);
    }
    
    public Tour getCurrentTour() {
        return currentTour;
    }
    

    public ArrayList<Long> getPositionIntersection() {
        return positionIntersection;
    }
    
    public void addDeliveryPoint(DeliveryPoint dp) {
        if(!currentDeliveryPoints.contains(dp)){
            this.currentDeliveryPoints.add(dp);
        }
    }

    public HashMap<Long, Tour> getListSegmentBetweenDPs() {
        return listSegmentBetweenDPs;
    }
    
    public Boolean removeDeliveryPoint(DeliveryPoint aDP) {
        for (DeliveryPoint dp : this.currentDeliveryPoints) {
            if (dp.getId().equals(aDP.getId())) {
                this.currentDeliveryPoints.remove(dp);
                return true;
            }
        }
        return false;
    }

    public HashMap<Long, HashMap<Long, Double>> getShortestPathBetweenDPs() {
        return shortestPathBetweenDPs;
    }
    
    public void addPositionIntersection(Long idIntersection) {
        this.positionIntersection.add(idIntersection);
    }
    
    public List<Segment> getListSegmentBetweenInters(Long idOrigin, Long idDest) {
        return listSegmentBetweenDPs.get(idOrigin).getListSegment(idDest);
    }
    
}