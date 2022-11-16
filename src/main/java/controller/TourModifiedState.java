/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
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
import xml.ExceptionXML;
import xml.PlanTextWriter;

import static view.GraphicalView.IntersectionType.*;
import static view.GraphicalView.IntersectionType.ON_TIME;
import view.Window;
import xml.XMLmapDeserializer;

public class TourModifiedState implements State {
    public void loadMapFromXML(Controller controller, Window window) throws ExceptionXML, ParserConfigurationException, SAXException, IOException {
        controller.map = XMLmapDeserializer.load(controller.map);
        controller.user = new User();
        Intersection warehouse = controller.getMap().getWarehouse();
        addWarehouse(warehouse, controller.user);
        controller.setCurrentState(controller.mapLoadedState);
        window.getGraphicalView().drawMap(controller.getMap());
        window.getInteractivePane().resetComboBoxes();
        window.getTextualView().updateData(controller.user, 1L);
        window.allowNode("COURIER_BOX", true);
        window.allowNode("TW_BOX", true);
        window.resetLateDeliveryNumber();
        window.setMessage("Please choose a courier and a time-window to start adding delivery points.");
    }

    /**
     * Method which adds the warehouse as the first node of the tour of all the couriers
     * @param warehouse the warehouse
     * @param user the user
     */
    private void addWarehouse(Intersection warehouse, User user) {
        DeliveryPoint dpWarehouse = new DeliveryPoint(warehouse.getId(), warehouse.getLatitude(), warehouse.getLongitude());
        for (Long key : user.getListCourier().keySet()) {
            Courier c = user.getListCourier().get(key);
            dpWarehouse.chooseCourier(c);
            c.addDeliveryPoint(dpWarehouse);

            c.addPositionIntersection(warehouse.getId());
            HashMap<Long, Double> nestedMap = new HashMap<>();
            nestedMap.put(warehouse.getId(), Double.valueOf("0.0"));
            c.getShortestPathBetweenDPs().put(warehouse.getId(), nestedMap);

            Tour tour = new Tour();
            tour.addTourRoute(dpWarehouse.getId(), new ArrayList<>());
            c.getListSegmentBetweenDPs().put(dpWarehouse.getId(), tour);
            user.getListCourier().replace(key, c);
        }
    }
    @Override
    public void modifyTourEnterDP(Controller controller, Courier c, Intersection intersection,
                                  Integer timeWindow, ListOfCommands loc) throws ParseException {
        loc.add(new EnterCommand(controller, controller.map, c, intersection, timeWindow));
        List<DeliveryPoint> listDPcopy = new ArrayList<>(c.getCurrentDeliveryPoints());

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

        DeliveryPoint dp = c.getCurrentDeliveryPoints().get(listDPcopy.size() - 1);
        //System.out.println("dp1: " + dp);
        int index = listDPcopy.indexOf(dp);
        // change currentTour
        DeliveryPoint headPoint = listDPcopy.get(index - 1);

        //System.out.println("headPoint:" + headPoint);

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

        // set estimatedDeliveryTime
        DeliveryPoint aDP = dp;

        long sum = headPoint.getEstimatedDeliveryTime().getTime();
        SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        while (aDP.getId() != null) {
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
        
        c.addTimeStampForDP(dp.getId(), dp.getEstimatedDeliveryTime());

        controller.getWindow().getGraphicalView().clearSelection();
        controller.getWindow().getTextualView().updateData(controller.user, c.getId());
        Integer lateDeliveries = controller.getWindow().getGraphicalView().updateCalculatedMap(controller.getMap(), c);
        controller.getWindow().updateOnCalculateTour(lateDeliveries);
        controller.getWindow().setMessage("Delivery point added.");
        controller.getWindow().allowNode("ADD_DP_TO_TOUR", false);
    }

    @Override
    public void modifyTourRemoveDP(Controller controller, Courier c, DeliveryPoint dp, ListOfCommands loc) throws ParseException {
        ArrayList<DeliveryPoint> listDPcopy = new ArrayList<>(c.getCurrentDeliveryPoints());        
        loc.add(new RemoveCommand(controller, controller.map, c, dp));
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
        Tour currentTour = c.getCurrentTour();
        
        // branch to Warehouse
        DeliveryPoint nextPoint = c.getCurrentDeliveryPoints().get(0); //warehouse
        
        if (index < listDPcopy.size() - 1) {            
            nextPoint = listDPcopy.get(index + 1);                       
        } 
        
        //update current tour
        List<Segment> newListSegmentBetween2Points = c.getListSegmentBetweenInters(headPoint.getId(), nextPoint.getId());
        currentTour.getTourRoute().replace(headPoint.getId(), newListSegmentBetween2Points);
        currentTour.getTourRoute().remove(dp.getId()); 
        c.removeTimeStampForDP(dp.getId());
        
        //update estimatedDeliveryTime
        if (!nextPoint.getId().equals(c.getCurrentDeliveryPoints().get(0).getId())) {
            long sum = headPoint.getEstimatedDeliveryTime().getTime();
            SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

            while (nextPoint.getId() != null) {
                Double dist = c.getShortestPathBetweenDPs().get(headPoint.getId()).get(nextPoint.getId());
                sum += (long) Math.ceil(5 * 60 * 1000 + dist * 60 * 1000);
                Date timeWin;
                if (nextPoint.getTimeWindow().compareTo(10) < 0) {
                    timeWin = sdf.parse(sd.format(headPoint.getEstimatedDeliveryTime()) + " 0" + nextPoint.getTimeWindow() + ":00:00");
                } else {
                    timeWin = sdf.parse(sd.format(headPoint.getEstimatedDeliveryTime()) + " " + nextPoint.getTimeWindow() + ":00:00");
                }
                Date estimatedTime = new Date(sum);
                if (estimatedTime.before(timeWin)) {
                    Date et = new Date(timeWin.getTime());
                    nextPoint.setEstimatedDeliveryTime(et);
                    sum = et.getTime();
                } else {
                    nextPoint.setEstimatedDeliveryTime(estimatedTime);
                }

                headPoint = nextPoint;
                if (listDPcopy.indexOf(nextPoint) == listDPcopy.size() - 1) {
                    break;
                }
                nextPoint = listDPcopy.get(listDPcopy.indexOf(nextPoint) + 1);
            }
        }
     
        controller.getWindow().getGraphicalView().clearSelection();
        controller.getWindow().getTextualView().updateData(controller.user, c.getId());
        Integer lateDeliveries = controller.getWindow().getGraphicalView().updateCalculatedMap(controller.getMap(), c);
        controller.getWindow().updateOnCalculateTour(lateDeliveries);
        controller.getWindow().setMessage("Delivery point removed.");
        controller.getWindow().allowNode("REMOVE_DP_FROM_TOUR", false);
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
        Long courierId = controller.getWindow().getInteractivePane().getSelectedCourierId();
        List<Long> dpIds = controller.user.getCourierById(controller.getWindow().getInteractivePane().getSelectedCourierId()).getDeliveryPointIds();
        dpIds.remove(0); // remove warehouse ID
        if (oldHoveredIntersection != null) {
            if (oldHoveredIntersection.equals(controller.getWindow().getGraphicalView().getSelectedIntersection())) {
                controller.getWindow().getGraphicalView().paintIntersection(oldHoveredIntersection, SELECTED);
            } else if (dpIds.contains(oldHoveredIntersection.getId())) {
                DeliveryPoint dp = controller.user.getCourierById(courierId).getDeliveryPointById(oldHoveredIntersection.getId());
                if (dp.getEstimatedDeliveryTime().getHours() > dp.getTimeWindow()) {
                    controller.getWindow().getGraphicalView().paintIntersection(dp, LATE);
                } else {
                    controller.getWindow().getGraphicalView().paintIntersection(dp, ON_TIME);
                }
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
                    DeliveryPoint dp = c.getDeliveryPointById(oldSelectedIntersection.getId());
                    if (dp.getEstimatedDeliveryTime().getHours() > dp.getTimeWindow()) {
                        controller.getWindow().getGraphicalView().paintIntersection(dp, LATE);
                    } else {
                        controller.getWindow().getGraphicalView().paintIntersection(dp, ON_TIME);
                    }
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
                if (oldSelectedDP.getEstimatedDeliveryTime().getHours() > oldSelectedDP.getTimeWindow()) {
                    controller.getWindow().getGraphicalView().paintIntersection(oldSelectedDP, LATE);
                } else {
                    controller.getWindow().getGraphicalView().paintIntersection(oldSelectedDP, ON_TIME);
                }
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