/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bbbbb
 */
public class Courier {
    private Long id;
    private String name;
    private List<DeliveryPoint> currentDeliveryPoints = new ArrayList<>();
    //private Tour currentTour;

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
    
    public void addDeliveryPoint(DeliveryPoint dp) {
        if(!currentDeliveryPoints.contains(dp)){
            this.currentDeliveryPoints.add(dp);
        }
    }
    
    public void removeDeliveryPoint(DeliveryPoint dp){
        this.currentDeliveryPoints.remove(dp);
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
    
    
}