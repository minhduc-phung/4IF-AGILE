/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import model.Courier;
import model.DeliveryPoint;
import model.Map;
import model.Segment;
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
    public void doCommand() throws ParseException{
        if (dp.getId().equals(map.getWarehouse().getId())) {
            return;
        }
        dp.chooseCourier(null);
        //dp.assignTimeWindow(null);
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
    public void undoCommand() throws ParseException{
        if (dp.getId().equals(map.getWarehouse().getId())) {
            
            return;
        }
        if (courier.getRemovedDP() != null) {
            dp = courier.getRemovedDP();
            System.out.println("dp ================================================ " + dp);
        }else{
            return;
        }

        /*}else{

        }*/
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
        //courier.addTimeStampForDP(dp.getId(), dp.getEstimatedDeliveryTime());
        
        
        //handle delivery time
        List<DeliveryPoint> listDPcopy = new ArrayList<>(courier.getCurrentDeliveryPoints());

        Collections.sort(listDPcopy, (DeliveryPoint d1, DeliveryPoint d2) -> {
            if (d1.getEstimatedDeliveryTime() == null || d2.getEstimatedDeliveryTime() == null) {
                if (d1.getTimeWindow().equals(d2.getTimeWindow())) {
                    return 0;
                } else if (d1.getTimeWindow().compareTo(d2.getTimeWindow()) > 0) {
                    return 1;
                } else {
                    return -1;
                }
            }else{
                if (d1.getEstimatedDeliveryTime().equals(d2.getEstimatedDeliveryTime())) {
                    return 0;
                } else if (d1.getEstimatedDeliveryTime().compareTo(d2.getEstimatedDeliveryTime()) > 0) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });

        DeliveryPoint dp = courier.getCurrentDeliveryPoints().get(listDPcopy.size() - 1);
        //System.out.println("dp1: " + dp);
        int index = listDPcopy.indexOf(dp);
        // change currentTour
        DeliveryPoint headPoint = listDPcopy.get(index - 1);

        //System.out.println("headPoint:" + headPoint);

        if (index < listDPcopy.size() - 1) {
            DeliveryPoint nextPoint = listDPcopy.get(index + 1);
            List<Segment> listSeg1 = courier.getListSegmentBetweenInters(headPoint.getId(), dp.getId());
            courier.getCurrentTour().getTourRoute().replace(headPoint.getId(), listSeg1);
            List<Segment> listSeg2 = courier.getListSegmentBetweenInters(dp.getId(), nextPoint.getId());
            courier.addCurrentTour(dp.getId(), listSeg2);
        } else {
            // branch to Warehouse
            DeliveryPoint nextPoint = courier.getCurrentDeliveryPoints().get(0); //warehouse
            List<Segment> listSeg1 = courier.getListSegmentBetweenInters(headPoint.getId(), dp.getId());
            courier.getCurrentTour().getTourRoute().replace(headPoint.getId(), listSeg1);
            List<Segment> listSeg2 = courier.getListSegmentBetweenInters(dp.getId(), nextPoint.getId());
            courier.addCurrentTour(dp.getId(), listSeg2);
        }        

        // set estimatedDeliveryTime
        DeliveryPoint aDP = dp;

        long sum = headPoint.getEstimatedDeliveryTime().getTime();
        SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        while (aDP.getId() != null) {
            Double dist = courier.getShortestPathBetweenDPs().get(headPoint.getId()).get(aDP.getId());
            sum += (long) Math.ceil(5 * 60 * 1000 + dist * 60 * 1000);
            Date timeWin;
            if (aDP.getTimeWindow().compareTo(10) < 0) {
                timeWin = sdf.parse(sd.format(headPoint.getEstimatedDeliveryTime()) + " 0" + aDP.getTimeWindow() + ":00:00");
            } else {
                timeWin = sdf.parse(sd.format(headPoint.getEstimatedDeliveryTime()) + " " + aDP.getTimeWindow() + ":00:00");
            }
            Date estimatedTime = new Date(sum);
            if (estimatedTime.before(timeWin)) {
                Date et = new Date(timeWin.getTime());
                aDP.setEstimatedDeliveryTime(et);
                sum = et.getTime();
            } else {
                aDP.setEstimatedDeliveryTime(estimatedTime);
            }

            headPoint = aDP;
            if (listDPcopy.indexOf(aDP) == listDPcopy.size()-1) {
                break;
            }
            aDP = listDPcopy.get(listDPcopy.indexOf(aDP)+1);
        }
        
        courier.addTimeStampForDP(dp.getId(), dp.getEstimatedDeliveryTime());
        courier.setRemovedDP(null);
        
        controller.getWindow().getGraphicalView().clearSelection();
        controller.getWindow().getGraphicalView().paintIntersection(dp, DP);
        controller.getWindow().getTextualView().updateData(controller.getUser(), courier.getId());
        controller.getWindow().setMessage("Delivery point returned.");
        
        Integer lateDeliveries = controller.getWindow().getGraphicalView().updateCalculatedMap(controller.getMap(), courier);
        controller.getWindow().updateOnCalculateTour(lateDeliveries);
        
    }
    
}
