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

public class EnterCommand implements Command {

    private Controller controller;
    private Map map;
    private Courier courier;
    private Intersection intersection;
    private Integer timeWindow;

    /**
     * Create the command to enter a delivery point in the tour
     * @param controller the controller
     * @param map the map
     * @param courier the chosen courier to add the delivery point
     * @param intersection the intersection where the delivery point is
     * @param timeWindow the time-window of the delivery point
     */
    public EnterCommand(Controller controller, Map map, Courier courier, Intersection intersection, Integer timeWindow) {
        this.controller = controller;
        this.map = map;
        this.courier = courier;
        this.intersection = intersection;
        this.timeWindow = timeWindow;
    }

    @Override
    public void doCommand() {
        DeliveryPoint dp;
        if (courier.getRemovedDP() == null) {
            dp = new DeliveryPoint(intersection.getId(), intersection.getLatitude(), intersection.getLongitude());
        } else {
            dp = courier.getRemovedDP();
            controller.getWindow().getGraphicalView().clearSelection();
            controller.getWindow().getTextualView().updateData(controller.user, courier.getId());
            Integer lateDeliveries = controller.getWindow().getGraphicalView().updateCalculatedMap(controller.getMap(), courier);
            controller.getWindow().updateOnCalculateTour(lateDeliveries);
            controller.getWindow().setMessage("Delivery point returned.");
            controller.getWindow().allowNode("ADD_DP_TO_TOUR", false);
        }
        dp.assignTimeWindow(timeWindow);
        dp.chooseCourier(courier);
        courier.addDeliveryPoint(dp);
        courier.addPositionIntersection(dp.getId());
        HashMap<Long, Double> nestedMap = new HashMap<>();
        nestedMap.put(dp.getId(), 0.0);

        Tour tour = new Tour();
        tour.addTourRoute(dp.getId(), new ArrayList<>());
        courier.getListSegmentBetweenDPs().put(dp.getId(), tour);
        courier.getShortestPathBetweenDPs().put(dp.getId(), nestedMap);
        courier.addShortestPathBetweenDP(map, dp);

    }

    @Override
    public void undoCommand() {
        int size = courier.getCurrentDeliveryPoints().size();
        DeliveryPoint dp = courier.getCurrentDeliveryPoints().get(size - 1);
        dp.chooseCourier(null);
        dp.assignTimeWindow(null);
        courier.removeDeliveryPoint(dp);
        courier.getPositionIntersection().remove(dp.getId());
        courier.removeShortestPathBetweenDP(map, dp);
        courier.setRemovedDP(dp);

        controller.getWindow().setMessage("Delivery point removed.");
        controller.getWindow().getTextualView().updateData(controller.user, courier.getId());
        controller.getWindow().getGraphicalView().paintIntersection(dp, UNSELECTED);
        Integer lateDeliveries = controller.getWindow().getGraphicalView().updateCalculatedMap(controller.getMap(), courier);
        controller.getWindow().updateOnCalculateTour(lateDeliveries);
        controller.getWindow().getTextualView().clearSelection();
        controller.getWindow().getGraphicalView().clearSelection();

    }

}
