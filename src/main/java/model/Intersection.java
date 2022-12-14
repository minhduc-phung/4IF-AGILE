/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.HashMap;

public class Intersection {
    protected Long id;
    protected Double latitude;
    protected Double longitude;
    protected HashMap<Long, Double> timeToConnectedIntersection;

    /**
     * Create an intersection with the given id, latitude and longitude
     * @param id the id of the intersection
     * @param latitude the latitude of the intersection
     * @param longitude the longitude of the intersection
     */
    public Intersection(Long id, Double latitude, Double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timeToConnectedIntersection = new HashMap<>();
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Intersection{" + "id=" + id + ", latitude=" + latitude + ", longitude=" + longitude + '}';
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HashMap<Long, Double> getTimeToConnectedIntersection() {
        return timeToConnectedIntersection;
    }
    
    public void addTravelTimeToNextIntersection (Long idIntersection, Double time) {
        this.timeToConnectedIntersection.put(idIntersection, time);
    }
}
