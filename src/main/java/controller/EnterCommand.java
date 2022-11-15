/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import model.Courier;
import model.DeliveryPoint;
import model.Intersection;
import model.Map;
import model.Tour;
import static view.GraphicalView.IntersectionType.DP;
import static view.GraphicalView.IntersectionType.UNSELECTED;

/**
 *
 * @author bbbbb
 */
public class EnterCommand implements Command {
    private Controller controller;
    private Map map;
    private Courier courier;
    private Intersection intersection;
    private Integer timeWindow;

    public EnterCommand(Controller controller, Map map, Courier courier, Intersection intersection, Integer timeWindow) {
        this.controller = controller;
        this.map = map;
        this.courier = courier;
        this.intersection = intersection;
        this.timeWindow = timeWindow;
    }

    @Override
    public void doCommand() {
        DeliveryPoint dp = new DeliveryPoint(intersection.getId(), intersection.getLatitude(), intersection.getLongitude());
        dp.assignTimeWindow(timeWindow);
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

    @Override
    public void undoCommand() {
        int size = courier.getCurrentDeliveryPoints().size();
        DeliveryPoint dp = courier.getCurrentDeliveryPoints().get(size-1);
        dp.chooseCourier(null);
        dp.assignTimeWindow(null);
        courier.removeDeliveryPoint(dp);
        courier.getPositionIntersection().remove(dp.getId());
        courier.removeShortestPathBetweenDP(map, dp);
       
        controller.getWindow().setMessage("Delivery point removed.");
        controller.getWindow().getGraphicalView().paintIntersection(dp, UNSELECTED);
        controller.getWindow().getTextualView().clearSelection();
        controller.getWindow().getGraphicalView().clearSelection();
        controller.getWindow().getTextualView().updateData(controller.user, courier.getId());
    }

}
