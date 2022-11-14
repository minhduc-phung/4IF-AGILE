/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import model.Courier;
import model.DeliveryPoint;
import model.Intersection;
import model.Map;
import model.Segment;
import model.User;
import org.xml.sax.SAXException;
import static view.GraphicalView.IntersectionType.DP;
import static view.GraphicalView.IntersectionType.HOVERED;
import static view.GraphicalView.IntersectionType.SELECTED;
import static view.GraphicalView.IntersectionType.UNSELECTED;
import xml.ExceptionXML;

/**
 *
 * @author bbbbb
 */
public class TourModifiedState implements State {
    @Override
    public void modifyTourEnterDP(Controller controller, Courier c, Intersection intersection,
                    Integer timeWindow, ListOfCommands loc) throws ParseException {
        loc.add(new EnterCommand(controller, controller.map, c, intersection, timeWindow));
        int i;
        List<DeliveryPoint> listDP = c.getCurrentDeliveryPoints();
        Integer minDiff = Integer.MAX_VALUE;
        DeliveryPoint headPoint = null;
        for (i = 0 ; i < listDP.size()-1 ; i++) {
            Integer diff = Math.abs(listDP.get(i).getTimeWindow() - timeWindow);
            if (diff.compareTo(minDiff) <= 0 && listDP.get(i).getTimeWindow().compareTo(timeWindow) <= 0) {
                minDiff = diff;
                headPoint = listDP.get(i);
            }
        }
        DeliveryPoint dp = listDP.get(listDP.size()-1);
        List<Segment> listSeg = c.getListSegmentBetweenInters(headPoint.getId(), dp.getId());
        List<Segment> listSeg2 = c.getCurrentTour().getListSegment(headPoint.getId());
        Intersection nextPoint = listSeg2.get(listSeg2.size()-1).getDestination();
        List<Segment> listSeg3 = c.getListSegmentBetweenInters(dp.getId(), nextPoint.getId());
        
        // change currentTour
        c.getCurrentTour().getTourRoute().replace(headPoint.getId(), listSeg);
        c.getCurrentTour().getTourRoute().put(dp.getId(), listSeg3);
        
        // set estimatedDeliveryTime
        DeliveryPoint aDP = dp;
        System.out.println("headpoint: " + headPoint + ", aDP:" + aDP);
        long sum = headPoint.getEstimatedDeliveryTime().getTime();
        SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        while ( !aDP.getId().equals(listDP.get(0).getId()) ) {
            Double dist = c.getShortestPathBetweenDPs().get(headPoint.getId()).get(aDP.getId());
            sum += (5*60*1000 + dist);
            Date timeWin;
            if ( dp.getTimeWindow().compareTo(10) < 0 ) {
                timeWin = sdf.parse(sd.format(headPoint.getEstimatedDeliveryTime()) + " 0" + dp.getTimeWindow() + ":00:00");
            } else {
                timeWin = sdf.parse(sd.format(headPoint.getEstimatedDeliveryTime()) + " " + dp.getTimeWindow() + ":00:00");
            }
            Date estimatedTime = new Date(sum);
            if (estimatedTime.before(timeWin)) {
                Date et = new Date(timeWin.getTime() + 5*60*1000);
                aDP.setEstimatedDeliveryTime(et);
            } else {
                System.out.println("aloo");
                aDP.setEstimatedDeliveryTime(estimatedTime);
            }
            listSeg2 = c.getCurrentTour().getListSegment(aDP.getId());
            nextPoint = listSeg2.get(listSeg2.size()-1).getDestination();
            headPoint = aDP;
            aDP.setId(nextPoint.getId());
        }
        
        controller.getWindow().getGraphicalView().clearSelection();
        controller.getWindow().getGraphicalView().paintIntersection(dp, DP);
        controller.getWindow().getTextualView().updateData(controller.user, c.getId());
        controller.getWindow().setMessage("Delivery point added.");
        controller.getWindow().allowNode("CALCULATE_TOUR", false);
    }
    
    @Override
    public void modifyTourRemoveDP(Controller controller, Courier c, DeliveryPoint dp, ListOfCommands loc) {
        loc.add(new RemoveCommand(controller, controller.map, c, dp));
    }
    
    @Override
    public void undo(ListOfCommands loc) {
        loc.undo();       
    }
    
    @Override
    public void generateDeliveryPlanForCourier(Controller controller, Courier c) throws ParserConfigurationException, SAXException, ExceptionXML,
                                                                                                IOException, TransformerException{
        controller.setCurrentState(controller.planGeneratedState);
    }
    
    @Override
    public void mouseMovedOnMap(Controller controller, double mousePosX, double mousePosY) {
        Double scale = controller.getWindow().getGraphicalView().getScale();
        Double minLongitude = controller.getWindow().getGraphicalView().getMinLongitude();
        Double minLatitude = controller.getWindow().getGraphicalView().getMinLatitude();
        Integer viewHeight = controller.getWindow().getGraphicalView().getViewHeight();
        // Map mouse position to latitude and longitude
        Double mouseLongtitude = mousePosX / scale + minLongitude;
        Double mouseLatitude = (viewHeight - mousePosY) / scale + minLatitude;
        Double minDistance = Double.MAX_VALUE;
        Intersection nearestIntersection = null;
        for (Intersection i : controller.getMap().getListIntersection().values()) {
            if (Objects.equals(i, controller.getMap().getWarehouse())) {
                continue;
            }
            Double distance = Math.sqrt(Math.pow(mouseLongtitude - i.getLongitude(), 2) + Math.pow(mouseLatitude - i.getLatitude(), 2));
            if (distance < minDistance) {
                minDistance = distance;
                nearestIntersection = i;
            }
        }
        Intersection oldHoveredIntersection = controller.getWindow().getGraphicalView().getHoveredIntersection();
        List<Long> dpIds = controller.user.getCourierById(controller.getWindow().getInteractivePane().getSelectedCourierId()).getDeliveryPointIds();
        dpIds.remove(0); // remove warehouse ID
        if (oldHoveredIntersection != null) {
            if (oldHoveredIntersection.equals(controller.getWindow().getGraphicalView().getSelectedIntersection())) {
                controller.getWindow().getGraphicalView().paintIntersection(oldHoveredIntersection, SELECTED);
            } else if (dpIds.contains(oldHoveredIntersection.getId())) {
                controller.getWindow().getGraphicalView().paintIntersection(oldHoveredIntersection, DP);
            } else {
                controller.getWindow().getGraphicalView().paintIntersection(oldHoveredIntersection, UNSELECTED);
           }
        }
        // Re-paint the selected intersection in case it gets overlapped by an old hovered intersection
        if (controller.getWindow().getGraphicalView().getSelectedIntersection() != null) {
            controller.getWindow().getGraphicalView().paintIntersection(controller.getWindow().getGraphicalView().getSelectedIntersection(), SELECTED);
        }

        controller.getWindow().getGraphicalView().setHoveredIntersection(nearestIntersection);

        controller.getWindow().getGraphicalView().paintIntersection(nearestIntersection, HOVERED);
    }

    @Override
    public void mouseClickedOnMap(Controller controller) {
        if (controller.getWindow().getGraphicalView().getHoveredIntersection() != null) {
            Long selectedCourierId = controller.getWindow().getInteractivePane().getSelectedCourierId();
            Courier c = controller.getUser().getCourierById(selectedCourierId);
            List<Long> dpIds = controller.user.getCourierById(controller.getWindow().getInteractivePane().getSelectedCourierId()).getDeliveryPointIds();
            Intersection oldSelectedIntersection = controller.getWindow().getGraphicalView().getSelectedIntersection();
            if (oldSelectedIntersection != null) {
                if (dpIds.contains(oldSelectedIntersection.getId())) {
                    controller.getWindow().getGraphicalView().paintIntersection(oldSelectedIntersection, DP);
                } else {
                    controller.getWindow().getGraphicalView().paintIntersection(oldSelectedIntersection, UNSELECTED);
                }
            }
            Intersection selectedIntersection = controller.getWindow().getGraphicalView().getHoveredIntersection();
            controller.getWindow().getGraphicalView().setSelectedIntersection(selectedIntersection);
            controller.getWindow().getTextualView().clearSelection();
            if (dpIds.contains(selectedIntersection.getId())) {
                controller.getWindow().getTextualView().setSelectedDeliveryPoint(c.getDeliveryPointById(selectedIntersection.getId()));
                controller.getWindow().getTextualView().getTableView().getSelectionModel().select(dpIds.indexOf(selectedIntersection.getId())-1);
                //controller.getWindow().allowNode("REMOVE_DP", true);
                controller.getWindow().allowNode("MODIFY_ENTER_DP", false);
            } else {
                //controller.getWindow().allowNode("REMOVE_DP", false);
                controller.getWindow().allowNode("MODIFY_ENTER_DP", true);
            }
            controller.getWindow().getGraphicalView().setHoveredIntersection(null);
            controller.getWindow().getGraphicalView().paintIntersection(controller.getWindow().getGraphicalView().getSelectedIntersection(), SELECTED);
        }
    }

    @Override
    public void mouseExitedMap(Controller controller){
        Intersection hoveredIntersection = controller.getWindow().getGraphicalView().getHoveredIntersection();
        if (hoveredIntersection != null) {
            if (hoveredIntersection.equals(controller.getWindow().getGraphicalView().getSelectedIntersection())) {
                controller.getWindow().getGraphicalView().paintIntersection(hoveredIntersection, SELECTED);
            } else {
                controller.getWindow().getGraphicalView().paintIntersection(hoveredIntersection, UNSELECTED);
            }
            controller.getWindow().getGraphicalView().setHoveredIntersection(null);
        }
    }

    @Override
    public void mouseClickedOnTable(Controller controller, int indexDP){
        Map map = controller.map;
        User user = controller.user;
        Courier courier = user.getCourierById(controller.getWindow().getInteractivePane().getSelectedCourierId());
        if (courier.getCurrentDeliveryPoints().size() > indexDP) {
            DeliveryPoint oldSelectedDP = controller.getWindow().getTextualView().getSelectedDeliveryPoint();
            if (oldSelectedDP != null){
                controller.getWindow().getGraphicalView().paintIntersection(oldSelectedDP, DP);
            }
            DeliveryPoint dp = courier.getCurrentDeliveryPoints().get(indexDP);
            controller.getWindow().getTextualView().setSelectedDeliveryPoint(dp);
            controller.getWindow().getGraphicalView().setSelectedIntersection(map.getIntersection(dp.getId()));
            controller.getWindow().getGraphicalView().paintIntersection(map.getIntersection(dp.getId()), SELECTED);
            controller.getWindow().allowNode("MODIFY_ENTER_DP", true);
        }
    }
}
