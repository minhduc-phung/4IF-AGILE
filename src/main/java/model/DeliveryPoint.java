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

    public DeliveryPoint(Date timeWindow, Date timestamp, Long id, Double latitude, Double longitude) {
        super(id, latitude, longitude);
        this.timeWindow = timeWindow;
        this.timestamp = timestamp;
    }
    
    public void chooseCourier(Courier c) {
        this.courier = c;
    }
}
