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
import model.Map;
import model.Tour;

/**
 *
 * @author bbbbb
 */
public class RemoveCommand implements Command {
    private Map map;
    private Courier courier;
    private DeliveryPoint dp;

    public RemoveCommand(Map map, Courier courier, DeliveryPoint dp) {
        this.map = map;
        this.courier = courier;
        this.dp = dp;
    }
    
    @Override
    public void doCommand() {
        if (dp.getId().equals(map.getWarehouse().getId())) {
            return;
        }
        dp.chooseCourier(null);
        courier.removeDeliveryPoint(dp);
        courier.getPositionIntersection().remove(dp.getId());
        courier.removeShortestPathBetweenDP(dp);
    }

    @Override
    public void undoCommand() {
        if (dp.getId().equals(map.getWarehouse().getId())) {
            return;
        }
        dp.chooseCourier(courier);
        courier.addDeliveryPoint(dp);
        courier.addPositionIntersection(dp.getId());
        HashMap<Long, Double> nestedMap = new HashMap<>();
        nestedMap.put(dp.getId(), 0.0);
        courier.getShortestPathBetweenDPs().put(dp.getId(), nestedMap);
        Tour tour = new Tour();
        tour.addTourRoute(dp.getId(), new ArrayList<>());
        courier.getListSegmentBetweenDPs().put(dp.getId(), tour);
        
        courier.addShortestPathBetweenDP(map, dp);
    }
    
}
