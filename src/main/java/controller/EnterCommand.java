/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.FileWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import model.Courier;
import model.DeliveryPoint;
import model.Intersection;
import model.Map;
import model.Segment;
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
    public void doCommand() throws ParseException {
        DeliveryPoint dp;
        if (courier.getRemovedDP() == null) {
            dp = new DeliveryPoint(intersection.getId(), intersection.getLatitude(), intersection.getLongitude());
        } else {
            dp = courier.getRemovedDP();
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
        
        if (courier.getRemovedDP() == null) {
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
                } else {
                    if (d1.getEstimatedDeliveryTime().equals(d2.getEstimatedDeliveryTime())) {
                        return 0;
                    } else if (d1.getEstimatedDeliveryTime().compareTo(d2.getEstimatedDeliveryTime()) > 0) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });

            DeliveryPoint newDP = courier.getCurrentDeliveryPoints().get(listDPcopy.size() - 1);
            //System.out.println("dp1: " + dp);
            int index = listDPcopy.indexOf(newDP);
            // change currentTour
            DeliveryPoint headPoint = listDPcopy.get(index - 1);

            //System.out.println("headPoint:" + headPoint);
            if (index < listDPcopy.size() - 1) {
                DeliveryPoint nextPoint = listDPcopy.get(index + 1);
                List<Segment> listSeg1 = courier.getListSegmentBetweenInters(headPoint.getId(), newDP.getId());
                courier.getCurrentTour().getTourRoute().replace(headPoint.getId(), listSeg1);
                List<Segment> listSeg2 = courier.getListSegmentBetweenInters(newDP.getId(), nextPoint.getId());
                courier.addCurrentTour(newDP.getId(), listSeg2);
            } else {
                // branch to Warehouse
                DeliveryPoint nextPoint = courier.getCurrentDeliveryPoints().get(0); //warehouse
                List<Segment> listSeg1 = courier.getListSegmentBetweenInters(headPoint.getId(), newDP.getId());
                courier.getCurrentTour().getTourRoute().replace(headPoint.getId(), listSeg1);
                List<Segment> listSeg2 = courier.getListSegmentBetweenInters(newDP.getId(), nextPoint.getId());
                courier.addCurrentTour(newDP.getId(), listSeg2);
            }

            // set estimatedDeliveryTime
            DeliveryPoint aDP = newDP;

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
                if (listDPcopy.indexOf(aDP) == listDPcopy.size() - 1) {
                    break;
                }
                aDP = listDPcopy.get(listDPcopy.indexOf(aDP) + 1);
            }

            courier.addTimeStampForDP(newDP.getId(), newDP.getEstimatedDeliveryTime());
        }
        
        controller.getWindow().getGraphicalView().clearSelection();
        controller.getWindow().getTextualView().updateData(controller.user, courier.getId());
        Integer lateDeliveries = controller.getWindow().getGraphicalView().updateCalculatedMap(controller.getMap(), courier);
        controller.getWindow().updateOnCalculateTour(lateDeliveries);
        controller.getWindow().setMessage("Delivery point returned.");
        controller.getWindow().allowNode("ADD_DP_TO_TOUR", false);


        courier.setRemovedDP(null);

    }

    @Override
    public void undoCommand() {
        int size = courier.getCurrentDeliveryPoints().size();
        ArrayList<DeliveryPoint> listDPcopy = new ArrayList<>(courier.getCurrentDeliveryPoints());
        DeliveryPoint dp = courier.getCurrentDeliveryPoints().get(size - 1);
        dp.chooseCourier(null);
        dp.assignTimeWindow(null);
        courier.removeDeliveryPoint(dp);
        courier.getPositionIntersection().remove(dp.getId());
        courier.removeShortestPathBetweenDP(map, dp);
        courier.setRemovedDP(dp);
        
        Collections.sort(listDPcopy, (DeliveryPoint d1, DeliveryPoint d2) -> {
            if (d1.getEstimatedDeliveryTime().equals(d2.getEstimatedDeliveryTime())) {
                return 0;
            } else if (d1.getEstimatedDeliveryTime().compareTo(d2.getEstimatedDeliveryTime()) > 0) {
                return 1;
            } else {
                return -1;
            }
        });
        
        int index = listDPcopy.indexOf(dp);
        DeliveryPoint headPoint = listDPcopy.get(index-1);
        Tour currentTour = courier.getCurrentTour();
        
        // branch to Warehouse
        DeliveryPoint nextPoint = courier.getCurrentDeliveryPoints().get(0); //warehouse
        
        if (index < listDPcopy.size() - 1) {            
            nextPoint = listDPcopy.get(index + 1);                       
        } 
        
        //update current tour
        List<Segment> newListSegmentBetween2Points = courier.getListSegmentBetweenInters(headPoint.getId(), nextPoint.getId());
        currentTour.getTourRoute().replace(headPoint.getId(), newListSegmentBetween2Points);
        currentTour.getTourRoute().remove(dp.getId()); 
        courier.removeTimeStampForDP(dp.getId());
        

        controller.getWindow().setMessage("Delivery point removed.");
        controller.getWindow().getTextualView().updateData(controller.user, courier.getId());
        controller.getWindow().getGraphicalView().paintIntersection(dp, UNSELECTED);
        Integer lateDeliveries = controller.getWindow().getGraphicalView().updateCalculatedMap(controller.getMap(), courier);
        controller.getWindow().updateOnCalculateTour(lateDeliveries);
        controller.getWindow().getTextualView().clearSelection();
        controller.getWindow().getGraphicalView().clearSelection();

    }

}
