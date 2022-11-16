/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import javax.xml.parsers.ParserConfigurationException;

import javafx.scene.control.ComboBox;
import javafx.scene.paint.Color;
import javax.xml.xpath.XPathExpressionException;
import tsp.CompleteGraph;
import model.Courier;
import model.DeliveryPoint;
import tsp.Graph;
import model.Intersection;
import model.Map;
import model.Segment;
import model.Tour;
import tsp.TSP;
import tsp.TSP1;
import model.User;
import org.xml.sax.SAXException;
import view.Window;
import xml.ExceptionXML;
import xml.XMLdpsDeserializer;
import xml.XMLmapDeserializer;

import static view.GraphicalView.IntersectionType.*;

/**
 *
 * @author bbbbb
 */
public class DPRestoredState implements State {
        
    @Override
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
    public void calculateTour(Controller controller, Courier c, Long idWarehouse) throws ParseException {
        int i;
        int nbVertices = c.getCurrentDeliveryPoints().size();
        Graph g = new CompleteGraph(c, idWarehouse);
        TSP tsp = new TSP1();
        tsp.searchSolution(20000, g);

        // take the earliest time window in ListCurrentDPs
        Integer earliestTW = c.getCurrentDeliveryPoints().get(0).getTimeWindow();

        Date now = new Date();
        SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        Date timeStamp = new Date();
        if (earliestTW < 10) {
            timeStamp = sdf.parse(sd.format(now) + " 0" + earliestTW + ":00:00");
        } else {
            timeStamp = sdf.parse(sd.format(now) + " " + earliestTW + ":00:00");
        }

        List<Integer> tspSolutions = new ArrayList<>();
        for (i = 0; i < nbVertices; i++) {
            tspSolutions.add(tsp.getSolution(i));
        }
        
        //timeStamp of warehouse
        DeliveryPoint dp = c.getCurrentDeliveryPoints().get(0);
        long sum = timeStamp.getTime();
        dp.setEstimatedDeliveryTime(timeStamp);
        
        //timeStamp of other DPs
        for (i = 0; i < tspSolutions.size() - 1; i++) {
            long timeInMinute = (long) Math.ceil(g.getCost(tspSolutions.get(i), tspSolutions.get(i + 1)) * 60 * 1000);
            sum += timeInMinute;
            dp = c.getCurrentDeliveryPoints().get(tspSolutions.get(i + 1));
            Date estimatedDeliveryTime = new Date(sum);
            Date timeWin = new Date();
            if ( dp.getTimeWindow().compareTo(10) < 0 ) {
                timeWin = sdf.parse(sd.format(now) + " 0" + dp.getTimeWindow() + ":00:00");
            } else {
                timeWin = sdf.parse(sd.format(now) + " " + dp.getTimeWindow() + ":00:00");
            }
            if (estimatedDeliveryTime.before(timeWin)) {
                sum = timeWin.getTime() + 5*60*1000;        //5 mins for delivery
                estimatedDeliveryTime.setTime(sum);
            } 
            dp.setEstimatedDeliveryTime(estimatedDeliveryTime);
            c.addTimeStampForDP(dp.getId(), dp.getEstimatedDeliveryTime());
        }

        // set currentTour
        for (i = 0; i < c.getCurrentDeliveryPoints().size() - 1; i++) {
            Long idCurrentInter = c.getCurrentDeliveryPoints().get(tspSolutions.get(i)).getId();
            Long idNextInter = c.getCurrentDeliveryPoints().get(tspSolutions.get(i + 1)).getId();
            List<Segment> listSeg = c.getListSegmentBetweenInters(idCurrentInter, idNextInter);
            for (Segment seg : listSeg) {
                controller.getWindow().getGraphicalView().paintArrow(seg, Color.web("0x00B0FF"));
            }
            c.addCurrentTour(idCurrentInter, listSeg);
        }
        Long idCurrentInter = c.getCurrentDeliveryPoints().get(tspSolutions.get(i)).getId();
        Long idNextInter = c.getCurrentDeliveryPoints().get(tspSolutions.get(0)).getId();
        List<Segment> listSeg = c.getListSegmentBetweenInters(idCurrentInter, idNextInter);
        c.addCurrentTour(idCurrentInter, listSeg);

        for (Segment seg : listSeg) {
            controller.getWindow().getGraphicalView().paintArrow(seg, Color.web("0x00B0FF"));
        }

        int lateDeliveryCount = 0;
        for (DeliveryPoint d : c.getCurrentDeliveryPoints()) {
            if (Objects.equals(d.getId(), idWarehouse)) {
                continue;
            }
            if (d.getEstimatedDeliveryTime().getHours() > d.getTimeWindow()) {
                controller.getWindow().getGraphicalView().paintIntersection(d, LATE);
                lateDeliveryCount++;
            } else {
                controller.getWindow().getGraphicalView().paintIntersection(d, ON_TIME);
            }
        }
        
        controller.getWindow().getTextualView().updateData(controller.user, c.getId());
        controller.getWindow().getTextualView().getTableView().sort();
        controller.getWindow().setMessage("The tour has been calculated.");
        controller.getWindow().allowNode("MODIFY_DP", true);
        controller.getWindow().allowNode("GENERATE_PLAN", true);
        controller.getWindow().allowNode("LOAD_MAP", false);
        controller.getWindow().allowNode("CALCULATE_TOUR", false);
        controller.getWindow().allowNode("RESTORE_DP", false);
        controller.getWindow().allowNode("SAVE_DP", false);
        controller.getWindow().updateOnCalculateTour(lateDeliveryCount);
        controller.setCurrentState(controller.tourCalculatedState);
    }
    
    @Override
    public void enterDeliveryPoint(Controller controller, Map map, Long idIntersection, Long idCourier, Integer timeWindow) {
        Intersection intersection = map.getIntersection(idIntersection);
        if (intersection.getId().equals(map.getWarehouse().getId())) {
            return;
        }
        Courier courier = controller.user.getCourierById(idCourier);
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

        controller.getWindow().getGraphicalView().clearSelection();
        controller.getWindow().getGraphicalView().paintIntersection(dp, DP);
        controller.getWindow().getTextualView().updateData(controller.getUser(), idCourier);
        controller.getWindow().setMessage("Delivery point added.");
        controller.getWindow().allowNode("VALIDATE_DP", false);
        controller.getWindow().allowNode("SAVE_DP", true);
        controller.getWindow().allowNode("CALCULATE_TOUR", true);
        controller.setCurrentState(controller.dpEnteredState);
    }

    @Override
    public void removeDeliveryPoint(Controller controller, Map map, DeliveryPoint dp, Long idCourier) {
        if (dp.getId().equals(map.getWarehouse().getId())) {
            return;
        }
        Courier courier = controller.user.getCourierById(idCourier);
        dp.chooseCourier(null);
        dp.assignTimeWindow(null);
        courier.removeDeliveryPoint(dp);
        courier.getPositionIntersection().remove(dp.getId());
        
        courier.removeShortestPathBetweenDP(map, dp);
        controller.getWindow().setMessage("Delivery point removed.");
        controller.getWindow().getGraphicalView().paintIntersection(dp, UNSELECTED);
        controller.getWindow().getTextualView().clearSelection();
        controller.getWindow().getGraphicalView().clearSelection();
        controller.getWindow().getTextualView().updateData(controller.user, idCourier);
        controller.getWindow().allowNode("REMOVE_DP", false);
        controller.getWindow().allowNode("CALCULATE_TOUR", true);
        controller.setCurrentState(controller.dpRemovedState);
    }
    
    @Override
    public void restoreDeliveryPointFromXML(Controller controller) throws ExceptionXML, ParserConfigurationException, IOException, 
                                                    SAXException, XPathExpressionException {
        //precondition : Map is loaded and XMLfile of deliveryPoints exists
        Map map = controller.map;
        User user = new User();
        
        controller.user = XMLdpsDeserializer.loadDPList(map, user);
        controller.getWindow().setMessage("Delivery points restored.");
        controller.setCurrentState(controller.dpRestoredState);
        controller.getWindow().allowNode("SAVE_DP", true);
        controller.getWindow().allowNode("CALCULATE_TOUR", true);
        ((ComboBox<String>) controller.getWindow().lookup("#COURIER_BOX")).setValue(controller.getUser().getListCourierName()[0]);
    }

    @Override
    public void selectCourier(Controller controller, Long idCourier) {
        controller.getWindow().getInteractivePane().setSelectedCourierId(idCourier);
        controller.getWindow().getTextualView().updateData(controller.getUser(), idCourier);
        controller.getWindow().getGraphicalView().clearSelection();
        controller.getWindow().setMessage("Courier " + controller.user.getCourierById(idCourier).getName() + " selected.");
        controller.getWindow().getGraphicalView().updateMap(controller.getMap(), controller.user.getCourierById(idCourier));
        controller.getWindow().allowNode("VALIDATE_DP", false);
        controller.getWindow().allowNode("REMOVE_DP", false);
        controller.getWindow().resetLateDeliveryNumber();
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
                controller.getWindow().allowNode("REMOVE_DP", true);
                controller.getWindow().allowNode("VALIDATE_DP", false);
            } else {
                controller.getWindow().allowNode("REMOVE_DP", false);
                controller.getWindow().allowNode("VALIDATE_DP", true);
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
            System.out.println(dp);
            controller.getWindow().getGraphicalView().setSelectedIntersection(map.getIntersection(dp.getId()));
            controller.getWindow().getGraphicalView().paintIntersection(map.getIntersection(dp.getId()), SELECTED);
            controller.getWindow().allowNode("VALIDATE_DP", false);
            controller.getWindow().allowNode("REMOVE_DP", true);
        }
    }
}
