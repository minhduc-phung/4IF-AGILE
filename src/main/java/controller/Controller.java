/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import model.Courier;
import model.DeliveryPoint;
import model.Intersection;
import model.Map;
import model.Segment;
import model.Tour;
import model.User;
import org.xml.sax.SAXException;
//import view.Window;
import view.Window;
import xml.ExceptionXML;

/**
 *
 * @author bbbbb
 */
public class Controller {
    protected User user = new User();
    protected Map map = new Map();
    private State currentState;
    private Window window;


    
    protected final InitialState initialState = new InitialState();
    protected final MapLoadedState mapLoadedState = new MapLoadedState();
    protected final CourierChosenState courierChosenState = new CourierChosenState();
    public final DPEnteredState dpEnteredState = new DPEnteredState();
    protected final DPRestoredState dpRestoredState = new DPRestoredState();
    protected final DPRemovedState dpRemovedState = new DPRemovedState();
    protected final TourCalculatedState tourCalculatedState = new TourCalculatedState();
    protected final PlanGeneratedState planGeneratedState = new PlanGeneratedState();
    protected final DPSavedState dpSavedState = new DPSavedState();   

    public Controller() {
        this.currentState = initialState;
        this.window = new Window(user, this);
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }
    
    public void loadMapFromXML() throws ParserConfigurationException, IOException, SAXException, ExceptionXML {
        this.currentState.loadMapFromXML(this, window);
        // print current state type
        System.out.println(currentState.getClass().getSimpleName());
    }
    
    public void addShortestPathBetweenDP(Map aMap, Courier c, DeliveryPoint aDP) {
        List<DeliveryPoint> listDP = c.getCurrentDeliveryPoints();
        HashMap<Long, Double> distanceFromADP = new HashMap<>();
        List<Segment> segmentFromADP = new ArrayList<>();
        Tour tour1 = new Tour();
        for (DeliveryPoint dp : listDP) {
            HashMap<Long, Long> precedentNode1 = new HashMap<>();
            Double dist = dijkstra(aMap, dp.getId(), aDP.getId(), precedentNode1);
            List<Segment> listSeg = new ArrayList<>();
            for (Long key : precedentNode1.keySet()) {
                if (precedentNode1.get(key) != null) {
                    Segment seg = aMap.getSegment(key, precedentNode1.get(key));
                    listSeg.add(seg);
                }
            }
            if (c.getShortestPathBetweenDPs().get(dp.getId()) == null){
                HashMap<Long, Double> nestedMap = new HashMap<Long, Double>();
                nestedMap.put(aDP.getId(), dist);
                c.getShortestPathBetweenDPs().replace(dp.getId(), nestedMap);
                
                Tour tour = new Tour();    
                tour.addTour(aDP.getId(), listSeg);
                c.getListSegmentBetweenDPs().replace(dp.getId(), tour);
            } else {
                c.getShortestPathBetweenDPs().get(dp.getId()).put(aDP.getId(), dist);
                Tour tour = new Tour();    
                tour.addTour(aDP.getId(), listSeg);
                c.getListSegmentBetweenDPs().put(aDP.getId(), tour);
            }
            HashMap<Long, Long> precedentNode2 = new HashMap<>();
            Double invertedDist = this.dijkstra(aMap, aDP.getId(), dp.getId(), precedentNode2);
            distanceFromADP.put(dp.getId(), invertedDist);
            for (Long key:precedentNode2.keySet()) {
                if (precedentNode2.get(key) != null) {
                    Segment seg = aMap.getSegment(key, precedentNode2.get(key));
                    segmentFromADP.add(seg);
                }
            }
            tour1.addTour(dp.getId(), listSeg);
        }
        c.getShortestPathBetweenDPs().put(aDP.getId(), distanceFromADP);
        c.getListSegmentBetweenDPs().put(aDP.getId(), tour1);
    }
    
    public void removeShortestPathBetweenDP(Courier c, DeliveryPoint aDP) {
        //System.out.println(c.removeDeliveryPoint(aDP));
        c.getShortestPathBetweenDPs().remove(aDP.getId());
        for (DeliveryPoint dp : c.getCurrentDeliveryPoints()) {
            c.getShortestPathBetweenDPs().get(dp.getId()).remove(aDP.getId());
        }
    }
    
    public Double dijkstra (Map aMap, Long idOrigin, Long idDest, HashMap<Long, Long> precedentNode) {
        List<Long> idWhiteNodes = new ArrayList<>();
        List<Long> idGreyNodes = new ArrayList<>();
        List<Long> idBlackNodes = new ArrayList<>();
        
        HashMap<Long, Double> distanceFromOrigin = new HashMap<>();
        
        for (Long id : aMap.getListIntersection().keySet()) {
            distanceFromOrigin.put(id, Double.MAX_VALUE);
            precedentNode.put(id, null);
            idWhiteNodes.add(id);
        }
        
        distanceFromOrigin.replace(idOrigin, 0.0);
        idGreyNodes.add(idOrigin);
        idWhiteNodes.remove(idOrigin);
        
        Long idGreyDistanceMin = Long.MAX_VALUE;
        Double distanceFromGreyMin = Double.MAX_VALUE;
        
        while (!idGreyNodes.isEmpty()) {
            for (Long idGrey : idGreyNodes) {
                if (distanceFromGreyMin > distanceFromOrigin.get(idGrey)) {
                    idGreyDistanceMin = idGrey;
                    distanceFromGreyMin = distanceFromOrigin.get(idGrey);
                }
            }
            Intersection greyMin = aMap.getIntersection(idGreyDistanceMin);
            for (Long idSucc : greyMin.getTimeToConnectedIntersection().keySet()) {
                if (idWhiteNodes.contains(idSucc) || idGreyNodes.contains(idSucc)) {
                    //next 5 lines: relacher()
                    Double newDistance = distanceFromOrigin.get(idGreyDistanceMin) + greyMin.getTimeToConnectedIntersection().get(idSucc);
                    if (distanceFromOrigin.get(idSucc) > newDistance) {
                        distanceFromOrigin.replace(idSucc, newDistance);
                        precedentNode.replace(idSucc, idGreyDistanceMin);
                    }
                    if (idWhiteNodes.contains(idSucc)) {
                        idWhiteNodes.remove(idSucc);
                        idGreyNodes.add(idSucc);
                    }
                }
            }
            idBlackNodes.add(idGreyDistanceMin);
            idGreyNodes.remove(idGreyDistanceMin);
            distanceFromGreyMin = Double.MAX_VALUE;
        }
        return distanceFromOrigin.get(idDest);
    }

    public void enterDeliveryPoint(Map map, Long idIntersection, Long idCourier, Integer timeWindow) {
        this.currentState.enterDeliveryPoint(this, map, idIntersection, idCourier, timeWindow);
    }

    public List<DeliveryPoint> restoreDeliveryPointFromXML(String XMLPathMap, String XMLPathDeliveryPoint, Date planDate)
                                                    throws ParserConfigurationException, IOException,
                                                    SAXException, XPathExpressionException, ExceptionXML {
        return this.currentState.restoreDeliveryPointFromXML(this, XMLPathMap, XMLPathDeliveryPoint, planDate);
    }

    public void calculateTour(Courier c, Long idWarehouse) {
        this.currentState.calculateTour(this, c, idWarehouse);
    }

    public void saveDeliveryPointToFile() throws XPathExpressionException, ParserConfigurationException, IOException, TransformerException, SAXException, ExceptionXML {
        this.currentState.saveDeliveryPointToFile(this);
    }

    public void removeDeliveryPoint(Map map, DeliveryPoint dp, Long idCourier) {
        this.currentState.removeDeliveryPoint(this, map, dp, idCourier);
    }

    public void generatePlan(Courier c) {
        this.currentState.generatedDeliveryPlanForCourier(this, c);
    }

    public void selectCourier(Long idCourier) {
        this.currentState.selectCourier(this, idCourier);
    }

    public void mouseClickedOnMap() {
        currentState.mouseClickedOnMap(this);
    }

    public void mouseMovedOnMap(double mousePosX, double mousePosY) {
        currentState.mouseMovedOnMap(this, mousePosX, mousePosY);
    }
    public void mouseExitedMap() {
        currentState.mouseExitedMap(this);
    }
    public Map getMap() {
        return map;
    }

    public User getUser() {
        return user;
    }

    public Window getWindow() {
        return window;
    }

}
