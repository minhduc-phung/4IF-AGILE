/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import model.Tour;
import model.User;
import org.xml.sax.SAXException;
import static view.GraphicalView.IntersectionType.DP;
import static view.GraphicalView.IntersectionType.HOVERED;
import static view.GraphicalView.IntersectionType.SELECTED;
import static view.GraphicalView.IntersectionType.UNSELECTED;
import xml.ExceptionXML;
import xml.PlanTextWriter;

/**
 *
 * @author bbbbb
 */
public class TourModifiedState implements State {

    @Override
    public void modifyTourEnterDP(Controller controller, Courier c, Intersection intersection,
                                  Integer timeWindow, ListOfCommands loc) throws ParseException {
        loc.add(new EnterCommand(controller, controller.map, c, intersection, timeWindow));
        List<DeliveryPoint> listDPcopy = c.getCurrentDeliveryPoints();
        Collections.sort(listDPcopy, (DeliveryPoint d1, DeliveryPoint d2) -> {
            if (d1.getTimeWindow().equals(d2.getTimeWindow())) {
                return 0;
            } else if (d1.getTimeWindow().compareTo(d2.getTimeWindow()) > 0) {
                return 1;
            } else {
                return -1;
            }
        });
        DeliveryPoint dp = c.getCurrentDeliveryPoints().get(listDPcopy.size() - 1);
        int index = listDPcopy.indexOf(dp);
        // change currentTour
        DeliveryPoint headPoint = listDPcopy.get(index - 1);
        for (DeliveryPoint d : c.getCurrentDeliveryPoints()) {
            if (d.getId().equals(headPoint.getId())) {
                headPoint = d;
                System.out.println(d);
                headPoint.setEstimatedDeliveryTime(d.getEstimatedDeliveryTime());
                break;
            }
        }

        if (index < listDPcopy.size() - 1) {
            DeliveryPoint nextPoint = listDPcopy.get(index + 1);
            List<Segment> listSeg1 = c.getListSegmentBetweenInters(headPoint.getId(), dp.getId());
            c.getCurrentTour().getTourRoute().replace(headPoint.getId(), listSeg1);
            List<Segment> listSeg2 = c.getListSegmentBetweenInters(dp.getId(), nextPoint.getId());
            c.addCurrentTour(dp.getId(), listSeg2);
        } else {
            // branch to Warehouse
            DeliveryPoint nextPoint = c.getCurrentDeliveryPoints().get(0); //warehouse
            List<Segment> listSeg1 = c.getListSegmentBetweenInters(headPoint.getId(), dp.getId());
            c.getCurrentTour().getTourRoute().replace(headPoint.getId(), listSeg1);
            List<Segment> listSeg2 = c.getListSegmentBetweenInters(dp.getId(), nextPoint.getId());
            c.addCurrentTour(dp.getId(), listSeg2);
        }
        /*try {
            FileWriter writer = new FileWriter("text.txt");
            for (Long key1 : c.getCurrentTour().getTourRoute().keySet()) {
                writer.write(key1 + ": " + c.getCurrentTour().getTourRoute().get(key1) + "\n\n");
            }
            writer.close();
        } catch (IOException ex) {
        }*/
        // set estimatedDeliveryTime
        DeliveryPoint aDP = dp;
        long sum = headPoint.getEstimatedDeliveryTime().getTime();
        SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

        while (!aDP.getId().equals(c.getCurrentDeliveryPoints().get(0).getId())) {
            Double dist = c.getShortestPathBetweenDPs().get(headPoint.getId()).get(aDP.getId());
            sum += (long) Math.ceil(5 * 60 * 1000 + dist * 60 * 1000);
            Date timeWin;
            if (aDP.getTimeWindow().compareTo(10) < 0) {
                timeWin = sdf.parse(sd.format(headPoint.getEstimatedDeliveryTime()) + " 0" + aDP.getTimeWindow() + ":00:00");
            } else {
                timeWin = sdf.parse(sd.format(headPoint.getEstimatedDeliveryTime()) + " " + aDP.getTimeWindow() + ":00:00");
            }
            Date estimatedTime = new Date(sum);
            if (estimatedTime.before(timeWin)) {
                Date et = new Date(timeWin.getTime() + 5 * 60 * 1000);
                aDP.setEstimatedDeliveryTime(et);
            } else {
                aDP.setEstimatedDeliveryTime(estimatedTime);
            }
            headPoint = aDP;
            int sizeSeg = c.getCurrentTour().getListSegment(aDP.getId()).size();
            Intersection inter = c.getCurrentTour().getListSegment(aDP.getId()).get(sizeSeg - 1).getDestination();
            aDP.setId(inter.getId());
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
    public void redo(ListOfCommands loc) {
        loc.redo();
    }

    @Override
    public void generateDeliveryPlanForCourier(Controller controller, Courier c) throws ParserConfigurationException, SAXException, ExceptionXML,
            IOException, TransformerException{
        Map map = controller.map;
        PlanTextWriter.getInstance().save(map, c);
        controller.getWindow().setMessage("Delivery plans saved.");
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
                controller.getWindow().getTextualView().getTableView().getSelectionModel().select(dpIds.indexOf(selectedIntersection.getId()) - 1);
                controller.getWindow().allowNode("REMOVE_DP_FROM_TOUR", true);
                controller.getWindow().allowNode("ADD_DP_TO_TOUR", false);
            } else {
                controller.getWindow().allowNode("REMOVE_DP_FROM_TOUR", false);
                controller.getWindow().allowNode("ADD_DP_TO_TOUR", true);
            }
            controller.getWindow().getGraphicalView().setHoveredIntersection(null);
            controller.getWindow().getGraphicalView().paintIntersection(controller.getWindow().getGraphicalView().getSelectedIntersection(), SELECTED);
        }
    }

    @Override
    public void mouseExitedMap(Controller controller) {
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
    public void mouseClickedOnTable(Controller controller, int indexDP) {
        Map map = controller.map;
        User user = controller.user;
        Courier courier = user.getCourierById(controller.getWindow().getInteractivePane().getSelectedCourierId());
        if (courier.getCurrentDeliveryPoints().size() > indexDP) {
            DeliveryPoint oldSelectedDP = controller.getWindow().getTextualView().getSelectedDeliveryPoint();
            if (oldSelectedDP != null) {
                controller.getWindow().getGraphicalView().paintIntersection(oldSelectedDP, DP);
            }
            DeliveryPoint dp = courier.getCurrentDeliveryPoints().get(indexDP);
            controller.getWindow().getTextualView().setSelectedDeliveryPoint(dp);
            controller.getWindow().getGraphicalView().setSelectedIntersection(map.getIntersection(dp.getId()));
            controller.getWindow().getGraphicalView().paintIntersection(map.getIntersection(dp.getId()), SELECTED);
            controller.getWindow().allowNode("REMOVE_DP_FROM_TOUR", true);
            controller.getWindow().allowNode("ADD_DP_TO_TOUR", false);
        }
    }
}