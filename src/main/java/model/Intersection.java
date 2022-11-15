/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.HashMap;

/**
 * this class defines the points on the map called intersections (it occurs as two road segments cross, defined by a latitude and a longitude and is modeled as a node in a graph)
 */
public class Intersection {
    protected Long id;
    protected Double latitude;
    protected Double longitude;
    protected HashMap<Long, Double> timeToConnectedIntersection;

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

    /**
     * this method add the necessary time to reach the next intersection
     * @param idIntersection the id of the intersection we want to reach
     * @param time the time necessary to reach the given intersection
     */
    public void addTravelTimeToNextIntersection (Long idIntersection, Double time) {
        this.timeToConnectedIntersection.put(idIntersection, time);
    }
}
