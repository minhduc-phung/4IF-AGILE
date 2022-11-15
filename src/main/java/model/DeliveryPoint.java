/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class represents the delivery point (it is associated with a courier, and it's composed by an intersection to which the delivery is to be made and the corresponding time-window. After computation, it also contains the timestamps)
 */
public class DeliveryPoint extends Intersection {
    /**
     * this attribute represents the time window : a 1-hour long time interval that starts at 8/9/10/11/… am.
     */
    private Integer timeWindow;
    /**
     * this attribute represents the precise computed time at which the courier should arrive at a delivery point
     */
    private Date timestamp;
    /**
     * this attribute represents the person in charge of the delivering
     */
    private Courier courier;

    /**
     * the constructor of this class
     * @param id
     * @param latitude
     * @param longitude
     */
    public DeliveryPoint(Long id, Double latitude, Double longitude) {
        super(id, latitude, longitude);
    }

    /**
     * this method allows us to choose a courier
     * @param c the chosen courier
     */
    public void chooseCourier(Courier c) {
        this.courier = c;
    }

    /**
     * this is a getter method for the attribute courier
     *
     */
    public Courier getCourier() {
        return courier;
    }

    /**
     * this is a getter method for the attribute time window
     *
     */
    public Integer getTimeWindow() {
        return timeWindow;
    }

    /**
     * this method assign a time window
     * @param t
     */
    public void assignTimeWindow(Integer t) {
        this.timeWindow = t;
    }

    /**
     * this method assign a time stamp
     * @param t
     */
    public void assignTimestamp(Date t) {
        this.timestamp = t;
    }

    /**
     *
     *
     */
    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "DeliveryPoint{" + "id="+id+ ", timeWindow=" + timeWindow + ", timestamp=" + timestamp + 
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

    public String getTimestampString() {
        return new SimpleDateFormat("HH:mm:ss").format(timestamp);

    }




    
    
}
