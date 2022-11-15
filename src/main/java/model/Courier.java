/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import observer.Observable;

/**
 *This class defines the person in charge of delivering
 */
public class Courier extends Observable {
    private Long id;
    private String name;
    /**
     * This attribute represents the list of the delivery points that should be transported by the current courier
     */
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

    /**
     * this is the getter method of the currentDeliveryPoints attribute
     * @return The list of the delivery points chosen by the current courier
     * @see Courier#getCurrentDeliveryPoints()
     */
    public List<DeliveryPoint> getCurrentDeliveryPoints() {
        return currentDeliveryPoints;
    }

    public String getName() {
        return name;
    }

    /**
     * this method allows us to add a route into the current tour.
     * @param idDeliveryPoint the id of the delivery point we want to add to the current
     * @param listSeg the list of segments relative to this delivery point
     */
    public void addCurrentTour(Long idDeliveryPoint, List<Segment> listSeg) {
        this.currentTour.addTourRoute(idDeliveryPoint, listSeg);
        this.notifyObservers();
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

    /**
     * this method add a delivery point to the ones that should be transported by the current courier if it's not already added
     * @param dp the delivery point to add
     */
    public void addDeliveryPoint(DeliveryPoint dp) {
        if(!currentDeliveryPoints.contains(dp)){
            this.currentDeliveryPoints.add(dp);
        }
    }

    public HashMap<Long, Tour> getListSegmentBetweenDPs() {
        return listSegmentBetweenDPs;
    }

    /**
     * this method remove a delivery point from the ones that should be transported by the current courier if it's existent.
     * @param aDP the delivery point to remove
     * @return true if the delivery point to remove found and removed, false otherwise
     */
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

    public List<Long> getDeliveryPointIds(){
        List<Long> ids = new ArrayList<>();
        for(DeliveryPoint dp : currentDeliveryPoints){
            ids.add(dp.getId());
        }
        return ids;
    }
}