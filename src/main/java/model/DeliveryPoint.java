/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author bbbbb
 */
public class DeliveryPoint extends Intersection {
    private Integer timeWindow;
    private double timestamp;
    private Courier courier;

    public DeliveryPoint(Long id, Double latitude, Double longitude) {
        super(id, latitude, longitude);
    }
    
    public void chooseCourier(Courier c) {
        this.courier = c;
    }

    public Courier getCourier() {
        return courier;
    }

    public Integer getTimeWindow() {
        return timeWindow;
    }
    
    public void assignTimeWindow(Integer t) {
        this.timeWindow = t;
    }
    
    public void assignTimestamp(double t) {
        this.timestamp = t;
    }

    public double getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "DeliveryPoint{" + "id="+id+ ", timeWindow=" + timeWindow + ", timestamp=" + timestamp + 
                ", courier=" + courier + '}';
    }




    
    
}
