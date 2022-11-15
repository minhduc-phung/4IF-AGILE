/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
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
import view.Window;
import xml.ExceptionXML;


/**
 * This class enables us to move between states (it transfers a state to another).
 * its methods are executed depending on the current state of the application.
 */

public class Controller {

    protected User user = new User();
    protected Map map = new Map();
    private State currentState;
    private Window window;

    protected final InitialState initialState = new InitialState();
    protected final MapLoadedState mapLoadedState = new MapLoadedState();
    protected final CourierChosenState courierChosenState = new CourierChosenState();
    protected final DPEnteredState dpEnteredState = new DPEnteredState();
    protected final DPRestoredState dpRestoredState = new DPRestoredState();
    protected final DPRemovedState dpRemovedState = new DPRemovedState();
    protected final TourCalculatedState tourCalculatedState = new TourCalculatedState();
    protected final PlanGeneratedState planGeneratedState = new PlanGeneratedState();
    protected final DPSavedState dpSavedState = new DPSavedState();

    /**
     * this is the constructor of the controller class it allows us to instantiate Controller objects
     */
    public Controller() {
        this.currentState = initialState;
        this.window = new Window(user, this);
    }
    /**
     * this is the setter for the currentState attribute
     * @param currentState
     */
    protected void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    /**
     * this method allows the user to load a Map from an XML file, it depends on the state of the application in a given moment so the loading of the map could be compatible with it.
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws ExceptionXML
     */
    public void loadMapFromXML() throws ParserConfigurationException, IOException, SAXException, ExceptionXML {
        this.currentState.loadMapFromXML(this, window);
    }

    /**
     * this method enables the application to add the shortest path between delivery points
     * @param aMap the loaded map
     * @param c the chosen courier
     * @param aDP
     */
    public void addShortestPathBetweenDP(Map aMap, Courier c, DeliveryPoint aDP) {
        // handle time-window
        Integer earliestTW = Integer.MAX_VALUE;
        Integer latestTW = Integer.MIN_VALUE;
        for (int k = 1; k < c.getCurrentDeliveryPoints().size(); k++) {
            DeliveryPoint aD = c.getCurrentDeliveryPoints().get(k);
            if (aD.getTimeWindow().compareTo(earliestTW) < 0) {
                earliestTW = aD.getTimeWindow();
            }
            if (aD.getTimeWindow().compareTo(latestTW) > 0) {
                latestTW = aD.getTimeWindow();
            }
        }

        List<DeliveryPoint> listDP = c.getCurrentDeliveryPoints();
        DeliveryPoint warehouse = listDP.get(0);
        warehouse.chooseCourier(c);
        if (earliestTW.equals(latestTW)) {
            warehouse.assignTimeWindow(earliestTW);
        } else {
            warehouse.assignTimeWindow(earliestTW - 1);
        }

        HashMap<Long, Double> distanceFromADP = new HashMap<>();
        Tour tour1 = new Tour();

        for (DeliveryPoint dp : listDP) {
            HashMap<Long, Long> precedentNode1 = new HashMap<>();
            Double dist = null;
            Tour tour = new Tour();
            List<Segment> listSeg = new ArrayList<>();
            if (dp.getTimeWindow().equals(aDP.getTimeWindow()) || dp.getTimeWindow().equals(aDP.getTimeWindow() - 1)) {
                dist = dijkstra(aMap, dp.getId(), aDP.getId(), precedentNode1);
                Long key = aDP.getId();
                while (precedentNode1.get(key) != null) {
                    Segment seg = aMap.getSegment(precedentNode1.get(key), key);
                    listSeg.add(seg);
                    key = precedentNode1.get(key);
                }
                Collections.reverse(listSeg);
                tour.addTourRoute(aDP.getId(), listSeg);

            } else {
                dist = Double.MAX_VALUE;
                tour.addTourRoute(aDP.getId(), listSeg);
            }

            if (c.getShortestPathBetweenDPs().get(dp.getId()) == null) {
                HashMap<Long, Double> nestedMap = new HashMap<Long, Double>();
                nestedMap.put(aDP.getId(), dist);
                c.getShortestPathBetweenDPs().replace(dp.getId(), nestedMap);
                c.getListSegmentBetweenDPs().replace(dp.getId(), tour);
            } else {
                c.getShortestPathBetweenDPs().get(dp.getId()).put(aDP.getId(), dist);
                c.getListSegmentBetweenDPs().get(dp.getId()).getTourRoute().put(aDP.getId(), listSeg);
            }

            HashMap<Long, Long> precedentNode2 = new HashMap<>();
            List<Segment> listSeg1 = new ArrayList<>();
            Double invertedDist = null;
            if (aDP.getTimeWindow().equals(dp.getTimeWindow()) || aDP.getTimeWindow().equals(dp.getTimeWindow()-1)) {
                invertedDist = this.dijkstra(aMap, aDP.getId(), dp.getId(), precedentNode2);
                Long key = dp.getId();
                while (precedentNode2.get(key) != null) {
                    Segment seg = aMap.getSegment(precedentNode2.get(key), key);
                    listSeg1.add(seg);
                    key = precedentNode2.get(key);
                }
                Collections.reverse(listSeg1);
                tour1.getTourRoute().put(dp.getId(), listSeg1);
            } else {
                invertedDist = Double.MAX_VALUE;
                tour1.getTourRoute().put(dp.getId(), listSeg1);
            }
            distanceFromADP.put(dp.getId(), invertedDist);

        }
        c.getShortestPathBetweenDPs().put(aDP.getId(), distanceFromADP);
        c.getListSegmentBetweenDPs().put(aDP.getId(), tour1);

        // add arc (latestNodes, warehouse)
        if (!earliestTW.equals(latestTW)) {
            for (DeliveryPoint deliveryPoint : c.getCurrentDeliveryPoints()) {
                if (deliveryPoint.getTimeWindow().equals(latestTW)) {
                    HashMap<Long, Long> precedentNode = new HashMap<>();
                    Double dist = this.dijkstra(aMap, deliveryPoint.getId(), warehouse.getId(), precedentNode);

                    List<Segment> listSeg = new ArrayList<>();
                    Long key = warehouse.getId();
                    while (precedentNode.get(key) != null) {
                        Segment seg = aMap.getSegment(precedentNode.get(key), key);
                        listSeg.add(seg);
                        key = precedentNode.get(key);
                    }
                    Collections.reverse(listSeg);
                    /*if (dp.getId().equals(1682387628L)) {
                        System.out.println("called");
                        System.out.println(listSeg);
                    }*/
                    c.getShortestPathBetweenDPs().get(deliveryPoint.getId()).replace(warehouse.getId(), dist);
                    c.getListSegmentBetweenDPs().get(deliveryPoint.getId()).getTourRoute().replace(warehouse.getId(), listSeg);
                } else {
                    c.getShortestPathBetweenDPs().get(deliveryPoint.getId()).replace(warehouse.getId(), Double.MAX_VALUE);
                    c.getListSegmentBetweenDPs().get(deliveryPoint.getId()).getTourRoute().replace(warehouse.getId(), new ArrayList<>());
                }
            }
        }
        warehouse.assignTimeWindow(8);
    }
    /**
     * this method enables the application to remove the shortest path between delivery points
     * @param c
     * @param aDP
     */
    public void removeShortestPathBetweenDP(Courier c, DeliveryPoint aDP) {
        c.getShortestPathBetweenDPs().remove(aDP.getId());
        c.getListSegmentBetweenDPs().remove(aDP.getId());
        for (DeliveryPoint dp : c.getCurrentDeliveryPoints()) {
            c.getShortestPathBetweenDPs().get(dp.getId()).remove(aDP.getId());
            c.getListSegmentBetweenDPs().get(dp.getId()).getTourRoute().remove(aDP.getId());
        }
    }


    public Double dijkstra(Map aMap, Long idOrigin, Long idDest, HashMap<Long, Long> precedentNode) {
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
    /**
     * this method allows us to enter a delivery point depending on the current state of the application.
     * @param map
     * @param idIntersection
     * @param idCourier
     * @param timeWindow
     */
    public void enterDeliveryPoint(Map map, Long idIntersection, Long idCourier, Integer timeWindow) {
        this.currentState.enterDeliveryPoint(this, map, idIntersection, idCourier, timeWindow);
    }

    /**
     * this method calculate the tour.
     * @param c
     * @param idWarehouse
     * @exception ParseException
     */
    public void calculateTour(Courier c, Long idWarehouse) throws ParseException {
        this.currentState.calculateTour(this, c, idWarehouse);
    }
    /**
     * this method saves the delivery points in a file.
     * @exception XPathExpressionException
     * @exception ParserConfigurationException
     * @exception IOException
     * @exception TransformerException
     * @exception SAXException
     * @exception ExceptionXML
     */
    public void saveDeliveryPointToFile() throws XPathExpressionException, ParserConfigurationException, IOException, TransformerException, SAXException, ExceptionXML {
        this.currentState.saveDeliveryPointToFile(this);
    }
    /**
     * this method restores the delivery points from a given XML file.
     * @exception IOException
     * @exception ParserConfigurationException
     */
    public void restoreDeliveryPointFromXML() throws ParserConfigurationException, IOException,
            SAXException, XPathExpressionException, ExceptionXML {
        this.currentState.restoreDeliveryPointFromXML(this);
    }
    /**
     * this method removes a selected delivery point.
     * @param map
     * @param dp
     * @param idCourier
     */
    public void removeDeliveryPoint(Map map, DeliveryPoint dp, Long idCourier) {
        this.currentState.removeDeliveryPoint(this, map, dp, idCourier);
    }
    /**
     * this method generates the delivery plan for a given courier.
     * @param c
     */
    public void generatePlan(Courier c) {
        this.currentState.generatedDeliveryPlanForCourier(this, c);
    }
    /**
     * this method selects a courier.
     * @param idCourier
     */
    public void selectCourier(Long idCourier) {
        this.currentState.selectCourier(this, idCourier);
    }
    /**
     * this method enables a point on the map to be selected by clicking on it, which changes its color on the map.
     */
    public void mouseClickedOnMap() {
        currentState.mouseClickedOnMap(this);
    }
    /**
     * this method shows us the nearest points when we move the mouse on the map (by changing their colors) which help us decide what point we're going to select.
     * @param mousePosX
     * @param mousePosY
     */
    public void mouseMovedOnMap(double mousePosX, double mousePosY) {
        currentState.mouseMovedOnMap(this, mousePosX, mousePosY);
    }

    /**
     * this method allows to exit the map.
     */
    public void mouseExitedMap() {
        currentState.mouseExitedMap(this);
    }

    /**
     * Getter method for the attribute map
     */
    public Map getMap() {
        return map;
    }
    /**
     * Getter method for the attribute user
     */
    public User getUser() {
        return user;
    }
    /**
     * Getter method for the attribute window
     */
    public Window getWindow() {
        return window;
    }
    /**
     * this method enables a delivery point on the table to be selected by clicking on it, which changes its color on the map.
     * @param indexDP
     */
    public void mouseClickedOnTable(int indexDP) {
        currentState.mouseClickedOnTable(this, indexDP);
    }
}
