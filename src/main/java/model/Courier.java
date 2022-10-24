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
 * @author nmngo
 */
public class Courier {
    private Long id;
    private String name;
    private List<DeliveryPoint> currentDeliveryPoints = new ArrayList<>();
    //private Tour currentTour;

    private ArrayList<Long> positionIntersection = new ArrayList<>();
    private HashMap<Long, HashMap<Long, Double>> shortestPathBetweenDPs;
    
    
    public Courier(Long id, String name) {
        this.id = id;
        this.name = name;
        this.shortestPathBetweenDPs = new HashMap<>();
    }

    public Long getId() {
        return id;
    }
    
    public List<DeliveryPoint> getCurrentDeliveryPoints() {
        return currentDeliveryPoints;
    }
    
    public void addDeliveryPoint(DeliveryPoint dp) {
        if(!currentDeliveryPoints.contains(dp)){
            this.currentDeliveryPoints.add(dp);
        }
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
    
    public void updateCurrentDeliveryPoints(List<DeliveryPoint> aList) {
        if (this.currentDeliveryPoints.isEmpty()) {
            for (DeliveryPoint d : aList) {
                this.currentDeliveryPoints = new ArrayList<>();
                this.currentDeliveryPoints.add(d);
            }
        } else {
            this.currentDeliveryPoints.clear();
            for (DeliveryPoint d : aList) {
                this.currentDeliveryPoints.add(d);
            }            
        }
    }

    public String getName() {
        return name;
    }
}