/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import dijkstra.Dijkstra;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import observer.Observable;


/**
 *This class defines the person in charge of delivering
 */
public class Courier extends Observable {

    private Long id;
    private String name;
    private List<DeliveryPoint> currentDeliveryPoints = new ArrayList<>();
    private HashMap<Long, Date> timeStampOfEachDP = new HashMap<>();
    private Tour currentTour = new Tour();
    private ArrayList<Long> positionIntersection = new ArrayList<>();
    private HashMap<Long, HashMap<Long, Double>> shortestPathBetweenDPs = new HashMap<>();
    private HashMap<Long, Tour> listSegmentBetweenDPs = new HashMap<>();
    
    private DeliveryPoint removedDP = null;

    /**
     * Create a courier with an id and a name
     * @param id the id of the courier
     * @param name the name of the courier
     */
    public Courier(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public DeliveryPoint getRemovedDP() {
        return removedDP;
    }

    public void setRemovedDP(DeliveryPoint removedDP) {
        this.removedDP = removedDP;
    }

    public List<DeliveryPoint> getCurrentDeliveryPoints() {
        return currentDeliveryPoints;
    }

    public String getName() {
        return name;
    }

    /**
     * Add a delivery point and the list of segments to arrive at that delivery point to the courier's tour
     * @param idDeliveryPoint the id of the delivery point
     * @param listSeg the list of segments to arrive at that delivery point
     */
    public void addCurrentTour(Long idDeliveryPoint, List<Segment> listSeg) {
        this.currentTour.addTourRoute(idDeliveryPoint, listSeg);
        this.notifyObservers();
    }

    /**
     * Get the list of segments to arrive at a delivery point with the id given in parameter
     * @param idDeliveryPoint the id of the delivery point
     * @return the list of segments to arrive at the delivery point
     */
    public List<Segment> getListSegmentCurrentTour(Long idDeliveryPoint) {
        return currentTour.getListSegment(idDeliveryPoint);
    }

    public Tour getCurrentTour() {
        return currentTour;
    }

    public ArrayList<Long> getPositionIntersection() {
        return positionIntersection;
    }

    public void addDeliveryPoint(DeliveryPoint dp) {
        if (!currentDeliveryPoints.contains(dp)) {
            this.currentDeliveryPoints.add(dp);
        }
    }

    public HashMap<Long, Tour> getListSegmentBetweenDPs() {
        return listSegmentBetweenDPs;
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

    /**
     * Add the shortest path from a delivery point to another which is calculated by the Dijkstra algorithm
     * @param aMap the map
     * @param aDP the delivery point
     */
    public void addShortestPathBetweenDP(Map aMap, DeliveryPoint aDP) {
        // handle time-window
        List<Integer> listTW = new ArrayList<>();
        List<DeliveryPoint> listDP = this.currentDeliveryPoints;
        DeliveryPoint warehouse = listDP.get(0);
        warehouse.chooseCourier(this);
        warehouse.assignTimeWindow(8);
        for (int k = 0; k < this.currentDeliveryPoints.size(); k++) {
            if (!listTW.contains(this.currentDeliveryPoints.get(k).getTimeWindow())) {
                listTW.add(this.currentDeliveryPoints.get(k).getTimeWindow());
            }
        }

        Collections.sort(listTW);
        for (DeliveryPoint dp : listDP) {
            HashMap<Long, Double> nestedMap = new HashMap<>();
            Tour tour = new Tour();
            int firstInd = listTW.indexOf(dp.getTimeWindow());
            for (DeliveryPoint d2 : listDP) {
                int secondInd = listTW.indexOf(d2.getTimeWindow());
                Double dist = Double.MAX_VALUE;
                List<Segment> listSeg = new ArrayList<>();
                if (firstInd == secondInd || firstInd == secondInd - 1) {
                    HashMap<Long, Long> precedentNode = new HashMap<>();
                    dist = Dijkstra.dijkstra(aMap, dp.getId(), d2.getId(), precedentNode);
                    Long key = d2.getId();
                    while (precedentNode.get(key) != null) {
                        Segment seg = aMap.getSegment(precedentNode.get(key), key);
                        listSeg.add(seg);
                        key = precedentNode.get(key);
                    }
                    Collections.reverse(listSeg);
                } else if (d2.getId().equals(warehouse.id) && firstInd == listTW.size() - 1) {
                    HashMap<Long, Long> precedentNode1 = new HashMap<>();
                    dist = Dijkstra.dijkstra(aMap, dp.getId(), warehouse.getId(), precedentNode1);
                    Long key = warehouse.getId();
                    while (precedentNode1.get(key) != null) {
                        Segment seg = aMap.getSegment(precedentNode1.get(key), key);
                        listSeg.add(seg);
                        key = precedentNode1.get(key);
                    }
                    Collections.reverse(listSeg);
                }
                nestedMap.put(d2.getId(), dist);
                tour.addTourRoute(d2.id, listSeg);
            }
            this.shortestPathBetweenDPs.replace(dp.id, nestedMap);
            this.listSegmentBetweenDPs.replace(dp.id, tour);
        }
    }

    /**
     * Remove a shortest path from a delivery point to another when a delivery point is removed
     * @param map the map
     * @param aDP the delivery point which is removed
     */
    public void removeShortestPathBetweenDP(Map map, DeliveryPoint aDP) {
        DeliveryPoint warehouse = this.currentDeliveryPoints.get(0);
        List<DeliveryPoint> listDP = this.currentDeliveryPoints;
        List<Integer> listTW = new ArrayList<>();
        for (int k = 0; k < this.currentDeliveryPoints.size(); k++) {
            if (!listTW.contains(this.currentDeliveryPoints.get(k).getTimeWindow())) {
                listTW.add(this.currentDeliveryPoints.get(k).getTimeWindow());
            }
        }
        Collections.sort(listTW);
        for (DeliveryPoint dp : listDP) {
            HashMap<Long, Double> nestedMap = new HashMap<>();
            Tour tour = new Tour();
            int firstInd = listTW.indexOf(dp.getTimeWindow());
            for (DeliveryPoint d2 : listDP) {
                int secondInd = listTW.indexOf(d2.getTimeWindow());
                Double dist = Double.MAX_VALUE;
                List<Segment> listSeg = new ArrayList<>();
                if (firstInd == secondInd || firstInd == secondInd - 1) {
                    HashMap<Long, Long> precedentNode = new HashMap<>();
                    dist = Dijkstra.dijkstra(map, dp.getId(), d2.getId(), precedentNode);
                    Long key = d2.getId();
                    while (precedentNode.get(key) != null) {
                        Segment seg = map.getSegment(precedentNode.get(key), key);
                        listSeg.add(seg);
                        key = precedentNode.get(key);
                    }
                    Collections.reverse(listSeg);
                } else if (d2.getId().equals(warehouse.id) && firstInd == listTW.size() - 1) {
                    HashMap<Long, Long> precedentNode1 = new HashMap<>();
                    dist = Dijkstra.dijkstra(map, dp.getId(), warehouse.getId(), precedentNode1);
                    Long key = warehouse.getId();
                    while (precedentNode1.get(key) != null) {
                        Segment seg = map.getSegment(precedentNode1.get(key), key);
                        listSeg.add(seg);
                        key = precedentNode1.get(key);
                    }
                    Collections.reverse(listSeg);
                }
                nestedMap.put(d2.getId(), dist);
                tour.addTourRoute(d2.id, listSeg);
            }
            this.shortestPathBetweenDPs.replace(dp.id, nestedMap);
            this.listSegmentBetweenDPs.replace(dp.id, tour);
        }

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

    public List<Long> getDeliveryPointIds() {
        List<Long> ids = new ArrayList<>();
        for (DeliveryPoint dp : currentDeliveryPoints) {
            ids.add(dp.getId());
        }
        return ids;
    }

    public DeliveryPoint getDeliveryPointById(Long id) {
        for (DeliveryPoint dp : currentDeliveryPoints) {
            if (dp.getId() == id) {
                return dp;
            }
        }
        return null;
    }

    public void addTimeStampForDP(Long idDP, Date date) {
        this.timeStampOfEachDP.put(idDP, date);
    }

    public String getTimeStampForDP(Long idDP) {
        return new SimpleDateFormat("HH:mm:ss").format(this.timeStampOfEachDP.get(idDP));
    }
    
    public void removeTimeStampForDP (Long idDP) {
        this.timeStampOfEachDP.remove(idDP);
    }
}
