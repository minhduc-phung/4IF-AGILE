/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author bbbbb
 */
public class DeliveryPoint extends Intersection {
    private Integer timeWindow;
    private Date estimatedDeliveryTime;
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
    
    public void setEstimatedDeliveryTime(Date t) {
        this.estimatedDeliveryTime = t;
    }

    public Date getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }

    @Override
    public String toString() {
        return "DeliveryPoint{" + "id="+id+ ", timeWindow=" + timeWindow + ", estimatedDeliveryTime=" + estimatedDeliveryTime + 
                ", courier=" + courier + '}';
    }

    public String getTimeWindowString() {
        // format the time window to be "HH:00 - HH:00"
        String timeWindowString = "";
        if (timeWindow < 9) {
            timeWindowString = "0" + timeWindow + ":00 - 0" + (timeWindow + 1) + ":00";
        } else if (timeWindow == 9){
            timeWindowString = "09:00 - 10:00";
        }
        else {
            timeWindowString = timeWindow + ":00 - " + (timeWindow + 1) + ":00";
        }
        return timeWindowString;
    }

    public String getEstimatedDeliveryTimeString() {
        return new SimpleDateFormat("HH:mm:ss").format(estimatedDeliveryTime);

    }




    
    
}
