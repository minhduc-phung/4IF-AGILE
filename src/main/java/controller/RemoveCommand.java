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
import static view.GraphicalView.IntersectionType.DP;
import static view.GraphicalView.IntersectionType.UNSELECTED;

public class RemoveCommand implements Command {
    private Controller controller;
    private Map map;
    private Courier courier;
    private DeliveryPoint dp;

    /**
     * Create the command to remove a delivery point from the tour
     * @param controller the controller
     * @param map the map
     * @param courier the courier who has the delivery point
     * @param dp the delivery point to remove
     */
    public RemoveCommand(Controller controller, Map map, Courier courier, DeliveryPoint dp) {
        this.controller = controller;
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
        dp.assignTimeWindow(null);
        courier.setRemovedDP(dp);
        courier.removeDeliveryPoint(dp);
        courier.getPositionIntersection().remove(dp.getId());
        courier.removeShortestPathBetweenDP(map, dp);
        
        controller.getWindow().setMessage("Delivery point removed.");
        controller.getWindow().getGraphicalView().paintIntersection(dp, UNSELECTED);
        controller.getWindow().getTextualView().clearSelection();
        controller.getWindow().getGraphicalView().clearSelection();
        Integer lateDeliveries = controller.getWindow().getGraphicalView().updateCalculatedMap(controller.getMap(), courier);
        controller.getWindow().updateOnCalculateTour(lateDeliveries);
        controller.getWindow().getTextualView().updateData(controller.user, courier.getId());
        controller.getWindow().allowNode("REMOVE_DP", false);
        controller.getWindow().allowNode("CALCULATE_TOUR", true);
    }

    @Override
    public void undoCommand() {
        if (dp.getId().equals(map.getWarehouse().getId())) {
            
            return;
        }
        if (courier.getRemovedDP() != null) {
            dp = courier.getRemovedDP();
            controller.getWindow().getGraphicalView().clearSelection();
            controller.getWindow().getTextualView().updateData(controller.user, courier.getId());
            controller.getWindow().setMessage("Delivery point returned.");
            controller.getWindow().allowNode("REMOVE_DP_FROM_TOUR", false);
        }else{
            controller.getWindow().getGraphicalView().clearSelection();
            controller.getWindow().getGraphicalView().paintIntersection(dp, DP);
            controller.getWindow().getTextualView().updateData(controller.getUser(), courier.getId());
            controller.getWindow().setMessage("Delivery point added.");
            controller.getWindow().allowNode("VALIDATE_DP", false);
            controller.getWindow().allowNode("SAVE_DP", true);
            controller.getWindow().allowNode("CALCULATE_TOUR", true);
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
        courier.addTimeStampForDP(dp.getId(), dp.getEstimatedDeliveryTime());
        Integer lateDeliveries = controller.getWindow().getGraphicalView().updateCalculatedMap(controller.getMap(), courier);
        controller.getWindow().updateOnCalculateTour(lateDeliveries);


    }
    
}
