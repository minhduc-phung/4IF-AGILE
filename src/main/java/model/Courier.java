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
 *
 * @author bbbbb
 */
public class Courier extends Observable {

    private Long id;
    private String name;
    private List<DeliveryPoint> currentDeliveryPoints = new ArrayList<>();
    private HashMap<Long, Date> timeStampOfEachDP = new HashMap<>();
    private Tour currentTour = new Tour();

    private ArrayList<Long> positionIntersection = new ArrayList<>();
    private HashMap<Long, HashMap<Long, Double>> shortestPathBetweenDPs = new HashMap<>();
    ;
    private HashMap<Long, Tour> listSegmentBetweenDPs = new HashMap<>();

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

    public String getName() {
        return name;
    }

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
}
