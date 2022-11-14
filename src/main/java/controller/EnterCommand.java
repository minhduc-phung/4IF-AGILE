/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.ArrayList;
import java.util.HashMap;
import model.Courier;
import model.DeliveryPoint;
import model.Intersection;
import model.Map;
import model.Tour;

/**
 *
 * @author bbbbb
 */
public class EnterCommand implements Command {
    private Map map;
    private Courier courier;
    private Intersection intersection;
    private Integer timeWindow;

    public EnterCommand(Map map, Courier courier, Intersection intersection, Integer timeWindow) {
        this.map = map;
        this.courier = courier;
        this.intersection = intersection;
        this.timeWindow = timeWindow;
    }

    @Override
    public void doCommand() {
        if (intersection.getId().equals(map.getWarehouse().getId())) {
            return;
        }
        DeliveryPoint dp = new DeliveryPoint(intersection.getId(), intersection.getLatitude(), intersection.getLongitude());
        dp.assignTimeWindow(timeWindow);
        dp.chooseCourier(courier);
        courier.addDeliveryPoint(dp);
        courier.addPositionIntersection(intersection.getId());
        HashMap<Long, Double> nestedMap = new HashMap<>();
        nestedMap.put(intersection.getId(), 0.0);
        courier.getShortestPathBetweenDPs().put(intersection.getId(), nestedMap);
        Tour tour = new Tour();
        tour.addTourRoute(intersection.getId(), new ArrayList<>());
        courier.getListSegmentBetweenDPs().put(intersection.getId(), tour);
        
        courier.addShortestPathBetweenDP(map, dp);
    }

    @Override
    public void undoCommand() {
        DeliveryPoint dp = new DeliveryPoint(intersection.getId(), intersection.getLatitude(), intersection.getLongitude());
        dp.chooseCourier(null);
        courier.removeDeliveryPoint(dp);
        courier.getPositionIntersection().remove(dp.getId());
        courier.removeShortestPathBetweenDP(dp);
    }

}
