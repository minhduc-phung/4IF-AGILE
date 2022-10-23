/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.Date;

/**
 *
 * @author bbbbb
 */
public class DeliveryPoint extends Intersection {
    private Date timeWindow;
    private Date timestamp;
    private Courier courier;
    private Date planDate;

    public DeliveryPoint(Date planDate, Long id, Double latitude, Double longitude) {
        super(id, latitude, longitude);
        this.planDate = planDate;
    }
    
    public void chooseCourier(Courier c) {
        this.courier = c;
    }

    public Courier getCourier() {
        return courier;
    }
    
    public void assignTimeWindow(Date t) {
        this.timeWindow = t;
    }
    
    public void assignTimestamp(Date t) {
        this.timestamp = t;
    }

    public Date getPlanDate() {
        return planDate;
    }
    
    
}
