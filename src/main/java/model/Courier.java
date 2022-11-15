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
    private HashMap <Long, Date> timeStampOfEachDP = new HashMap<>();
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
        if(!currentDeliveryPoints.contains(dp)){
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
        listTW.add(8);
        for (int k = 1; k < this.currentDeliveryPoints.size(); k++) {
            if ( !listTW.contains(this.currentDeliveryPoints.get(k).getTimeWindow()) ) {
                listTW.add( this.currentDeliveryPoints.get(k).getTimeWindow() );
            }
        }
        Collections.sort(listTW);
        int sizeTW = listTW.size();
        Integer earliestTW = listTW.get(0);
        Integer latestTW = listTW.get(sizeTW-1);
        
        List<DeliveryPoint> listDP = this.currentDeliveryPoints;
        DeliveryPoint warehouse = listDP.get(0);
        warehouse.chooseCourier(this);
        warehouse.assignTimeWindow(8);

        HashMap<Long, Double> distanceFromADP = new HashMap<>();
        Tour tour1 = new Tour();

        for (DeliveryPoint dp : listDP) {
            HashMap<Long, Long> precedentNode1 = new HashMap<>();
            Double dist = null;
            List<Segment> listSeg = new ArrayList<>();
            int index = listTW.indexOf(dp.getTimeWindow());
            int indexNextTW = index + 1;
            if (  dp.getTimeWindow().equals(aDP.getTimeWindow()) || listTW.indexOf(aDP.getTimeWindow()) == indexNextTW ) {
                dist = Dijkstra.dijkstra(aMap, dp.getId(), aDP.getId(), precedentNode1);
                Long key = aDP.getId();
                while (precedentNode1.get(key) != null) {
                    Segment seg = aMap.getSegment(precedentNode1.get(key), key);
                    listSeg.add(seg);
                    key = precedentNode1.get(key);
                }
                Collections.reverse(listSeg);
            } else {
                dist = Double.MAX_VALUE;
            }
            
            this.shortestPathBetweenDPs.get(dp.getId()).put(aDP.getId(), dist);
            this.listSegmentBetweenDPs.get(dp.getId()).getTourRoute().put(aDP.getId(), listSeg);

            HashMap<Long, Long> precedentNode2 = new HashMap<>();
            List<Segment> listSeg1 = new ArrayList<>();
            Double invertedDist = null;
            index = listTW.indexOf(aDP.getTimeWindow());
            indexNextTW = index + 1;
            if (   aDP.getTimeWindow().equals(dp.getTimeWindow()) || listTW.indexOf(dp.getTimeWindow()) == indexNextTW  ) {
                invertedDist = Dijkstra.dijkstra(aMap, aDP.getId(), dp.getId(), precedentNode2);
                Long key = dp.getId();
                while (precedentNode2.get(key) != null) {
                    Segment seg = aMap.getSegment(precedentNode2.get(key), key);
                    listSeg1.add(seg);
                    key = precedentNode2.get(key);
                }
                Collections.reverse(listSeg1);
            } else {
                invertedDist = Double.MAX_VALUE;
            }
            distanceFromADP.put(dp.getId(), invertedDist);
            tour1.addTourRoute(dp.getId(), listSeg1);
        }
        this.shortestPathBetweenDPs.put(aDP.getId(), distanceFromADP);
        this.listSegmentBetweenDPs.put(aDP.getId(), tour1);

        // add arc (latestNodes, warehouse)
        if (!earliestTW.equals(latestTW)) {
            for (DeliveryPoint deliveryPoint : this.currentDeliveryPoints) {
                if (deliveryPoint.getTimeWindow().equals(latestTW)) {
                    HashMap<Long, Long> precedentNode = new HashMap<>();
                    Double dist = Dijkstra.dijkstra(aMap, deliveryPoint.getId(), warehouse.getId(), precedentNode);

                    List<Segment> listSeg = new ArrayList<>();
                    Long key = warehouse.getId();
                    while (precedentNode.get(key) != null) {
                        Segment seg = aMap.getSegment(precedentNode.get(key), key);
                        listSeg.add(seg);
                        key = precedentNode.get(key);
                    }
                    Collections.reverse(listSeg);
                    this.shortestPathBetweenDPs.get(deliveryPoint.getId()).replace(warehouse.getId(), dist);
                    this.listSegmentBetweenDPs.get(deliveryPoint.getId()).getTourRoute().replace(warehouse.getId(), listSeg);
                } else {
                    this.shortestPathBetweenDPs.get(deliveryPoint.getId()).replace(warehouse.getId(), Double.MAX_VALUE);
                    this.listSegmentBetweenDPs.get(deliveryPoint.getId()).getTourRoute().replace(warehouse.getId(), new ArrayList<>());
                }
            }
        }
    }

    public void removeShortestPathBetweenDP(DeliveryPoint aDP) {
        this.shortestPathBetweenDPs.remove(aDP.getId());
        this.listSegmentBetweenDPs.remove(aDP.getId());
        for (DeliveryPoint dp : this.currentDeliveryPoints) {
            this.shortestPathBetweenDPs.get(dp.getId()).remove(aDP.getId());
            this.listSegmentBetweenDPs.get(dp.getId()).getTourRoute().remove(aDP.getId());
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

    public List<Long> getDeliveryPointIds(){
        List<Long> ids = new ArrayList<>();
        for(DeliveryPoint dp : currentDeliveryPoints){
            ids.add(dp.getId());
        }
        return ids;
    }

    public DeliveryPoint getDeliveryPointById(Long id){
        for (DeliveryPoint dp: currentDeliveryPoints){
            if (dp.getId() == id){
                return dp;
            }
        }
        return null;
    }
    
    public void addTimeStampForDP (Long idDP, Date date) {
        this.timeStampOfEachDP.put(idDP, date);
    }
    
    public String getTimeStampForDP (Long idDP) {
        return new SimpleDateFormat("HH:mm:ss").format(this.timeStampOfEachDP.get(idDP));
    }
}