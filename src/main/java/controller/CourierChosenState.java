/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import javax.xml.parsers.ParserConfigurationException;

import javafx.scene.paint.Color;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import model.Courier;
import model.DeliveryPoint;
import model.Intersection;
import model.Map;
import model.Tour;
import model.User;
import org.xml.sax.SAXException;
import view.Window;
import xml.ExceptionXML;
import xml.XMLdpsDeserializer;
import xml.XMLdpsSerializer;
import xml.XMLmapDeserializer;

/**
 * This class is for the state where the courier is already chosen.
 * Its methods are executed in the Controller class when the current state is CourierChosenState
 */
public class CourierChosenState implements State {
    /**
     * This method allow us to enter a delivery point
     * @param controller
     * @param map
     * @param idIntersection
     * @param idCourier
     * @param timeWindow
     */
    @Override
    public void enterDeliveryPoint(Controller controller, Map map, Long idIntersection, Long idCourier, Integer timeWindow) {
        Intersection i = map.getIntersection(idIntersection);
        if (idIntersection.equals(map.getWarehouse().getId())) {
            return;
        }
        DeliveryPoint dp = new DeliveryPoint(idIntersection, i.getLatitude(), i.getLongitude());
        Courier c = controller.user.getCourierById(idCourier);
        dp.assignTimeWindow(timeWindow);
        dp.chooseCourier(c);
        c.addDeliveryPoint(dp);
        c.addPositionIntersection(idIntersection);
        if (!c.getShortestPathBetweenDPs().isEmpty()) {
            controller.addShortestPathBetweenDP(map, c, dp);
        } else {
            c.getShortestPathBetweenDPs().put(dp.getId(), new HashMap<>());
            c.getListSegmentBetweenDPs().put(dp.getId(), new Tour());
        }
        
        controller.getWindow().getGraphicalView().clearSelection();
        controller.getWindow().getGraphicalView().paintIntersection(dp, Color.BLUE, map);
        controller.getWindow().getTextualView().updateData(controller.getUser(), idCourier);
        controller.getWindow().setMessage("Delivery point added.");
        controller.getWindow().allowNode("VALIDATE_DP", false);
        controller.getWindow().allowNode("SAVE_DP", true);
        controller.getWindow().allowNode("CALCULATE_TOUR", true);
        controller.setCurrentState(controller.dpEnteredState);
    }
    /**
     * this method allows the user to select a courier from those existent
     * @param controller
     * @param idCourier the id of the courier to select
     * @see model.Courier
     */
    @Override
    public void selectCourier(Controller controller, Long idCourier) {
        controller.getWindow().getInteractivePane().setSelectedCourierId(idCourier);
        controller.getWindow().getTextualView().updateData(controller.getUser(), idCourier);
        controller.getWindow().getTextualView().clearSelection();
        controller.getWindow().setMessage("Courier " + controller.user.getCourierById(idCourier).getName() + " selected.");
        controller.getWindow().getGraphicalView().updateMap(controller.getMap(), controller.user.getCourierById(idCourier));
    }
    /**
     * this method allows us to load a map from an xml file
     * @param controller
     * @param window
     * @throws ExceptionXML
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    @Override
    public void loadMapFromXML(Controller controller, Window window) throws ExceptionXML, ParserConfigurationException, SAXException, IOException {
        controller.map = XMLmapDeserializer.load(controller.map);
        controller.user = new User();
        Intersection warehouse = controller.getMap().getWarehouse();
        addWarehouse(warehouse, controller.user);
        controller.setCurrentState(controller.mapLoadedState);
        window.getGraphicalView().drawMap(controller.getMap());
        window.allowNode("COURIER_BOX", true);
        window.allowNode("TW_BOX", true);
        window.setMessage("Please choose a courier and a time-window to start adding delivery points.");
    }

    /**
     * this method allows us to add a warehouse
     * @param warehouse the intersection we want to add as a warehouse
     * @param user the user of this application
     * @see model.User
     * @see model.Map the class Map : warehouse is one its attributes
     */
    private void addWarehouse (Intersection warehouse, User user) {
        DeliveryPoint dpWarehouse = new DeliveryPoint(warehouse.getId(), warehouse.getLatitude(), warehouse.getLongitude());
        for (Long key : user.getListCourier().keySet()) {
            Courier c = user.getListCourier().get(key);
            dpWarehouse.chooseCourier(c);
            c.addDeliveryPoint(dpWarehouse);
            
            c.addPositionIntersection(warehouse.getId());
            HashMap<Long, Double> nestedMap = new HashMap<>();
            nestedMap.put(warehouse.getId(), Double.valueOf("0.0"));
            c.getShortestPathBetweenDPs().put(warehouse.getId(), nestedMap);
            user.getListCourier().replace(key, c);
            
            Tour tour = new Tour();
            tour.addTourRoute(warehouse.getId(), null);
            c.getListSegmentBetweenDPs().put(warehouse.getId(), tour);
        }
    }

    /**
     * this method allows us to save delivery points into an xml file
     * @param controller
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws ExceptionXML
     * @throws IOException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     * @throws XPathExpressionException
     */
    @Override
    public void saveDeliveryPointToFile(Controller controller) throws ParserConfigurationException, SAXException, ExceptionXML,
                                        IOException, TransformerConfigurationException, TransformerException, XPathExpressionException {
        Map map = controller.map;
        User user = controller.user;
        XMLdpsSerializer.getInstance().save(map, user);
        controller.getWindow().setMessage("Delivery points saved.");
        controller.setCurrentState(controller.dpSavedState);
    }
    /**
     * this method allows us to restore delivery points from an XML file
     * @param controller
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws ExceptionXML
     * @throws IOException
     * @throws XPathExpressionException
     */
    @Override
    public void restoreDeliveryPointFromXML(Controller controller) 
                                                    throws ExceptionXML, ParserConfigurationException, IOException, 
                                                    SAXException, XPathExpressionException {
        //precondition : Map is loaded and XMLfile of deliveryPoints exists
        Map map = controller.map;
        User user = new User();
        controller.user = XMLdpsDeserializer.loadDPList(map, user);
        controller.getWindow().setMessage("Delivery points restored.");
        controller.setCurrentState(controller.dpRestoredState);
        controller.getWindow().allowNode("SAVE_DP", true);
        controller.getWindow().allowNode("CALCULATE_TOUR", true);
    }

    /**
     * this method shows us the nearest intersections whenever we move the mouse on the map (by changing their colors) which help us decide what intersection we're going to select.
     * @param controller
     * @param mousePosX
     * @param mousePosY
     */
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
                controller.getWindow().getGraphicalView().paintIntersection(oldHoveredIntersection, Color.BROWN, controller.getMap());
            } else if (dpIds.contains(oldHoveredIntersection.getId())) {
                controller.getWindow().getGraphicalView().paintIntersection(oldHoveredIntersection, Color.BLUE, controller.getMap());
            } else {
                controller.getWindow().getGraphicalView().paintIntersection(oldHoveredIntersection, Color.WHITE, controller.getMap());
           }
        }
        // Re-paint the selected intersection in case it gets overlapped by an old hovered intersection
        if (controller.getWindow().getGraphicalView().getSelectedIntersection() != null) {
            controller.getWindow().getGraphicalView().paintIntersection(controller.getWindow().getGraphicalView().getSelectedIntersection(), Color.BROWN, controller.getMap());
        }

        controller.getWindow().getGraphicalView().setHoveredIntersection(nearestIntersection);

        controller.getWindow().getGraphicalView().paintIntersection(nearestIntersection, Color.ORANGE, controller.getMap());
    }

    /**
     * this method enables an intersection on the map to be selected by clicking on it, which changes its color.
     * @param controller
     */
    @Override
    public void mouseClickedOnMap(Controller controller) {

        if (controller.getWindow().getGraphicalView().getHoveredIntersection() != null) {
            List<Long> dpIds = controller.user.getCourierById(controller.getWindow().getInteractivePane().getSelectedCourierId()).getDeliveryPointIds();
            Intersection oldSelectedIntersection = controller.getWindow().getGraphicalView().getSelectedIntersection();
            if (oldSelectedIntersection != null) {
                if (dpIds.contains(oldSelectedIntersection.getId())) {
                    controller.getWindow().getGraphicalView().paintIntersection(oldSelectedIntersection, Color.BLUE, controller.getMap());
                } else {
                    controller.getWindow().getGraphicalView().paintIntersection(oldSelectedIntersection, Color.WHITE, controller.getMap());
                }
            }
            controller.getWindow().getGraphicalView().setSelectedIntersection(controller.getWindow().getGraphicalView().getHoveredIntersection());
            controller.getWindow().getGraphicalView().setHoveredIntersection(null);
            controller.getWindow().getTextualView().clearSelection();
            controller.getWindow().getGraphicalView().paintIntersection(controller.getWindow().getGraphicalView().getSelectedIntersection(), Color.BROWN, controller.getMap());
            controller.getWindow().allowNode("VALIDATE_DP", true);
            controller.getWindow().allowNode("REMOVE_DP", false);
        }
    }
    /**
     * this method allows us to exit the mouse from the map
     * @param controller
     */
    @Override
    public void mouseExitedMap(Controller controller){
        Intersection hoveredIntersection = controller.getWindow().getGraphicalView().getHoveredIntersection();
        if (hoveredIntersection != null) {
            if (hoveredIntersection.equals(controller.getWindow().getGraphicalView().getSelectedIntersection())) {
                controller.getWindow().getGraphicalView().paintIntersection(hoveredIntersection, Color.BROWN, controller.getMap());
            } else {
                controller.getWindow().getGraphicalView().paintIntersection(hoveredIntersection, Color.WHITE, controller.getMap());
            }
            controller.getWindow().getGraphicalView().setHoveredIntersection(null);
        }
    }

    /**
     * this method allows us to click a delivery point on the table
     * @param controller
     * @param indexDP the index of the delivery point we want to select on the table
     */
    @Override
    public void mouseClickedOnTable(Controller controller, int indexDP){
        Map map = controller.map;
        User user = controller.user;
        Courier courier = user.getCourierById(controller.getWindow().getInteractivePane().getSelectedCourierId());
        if (courier.getCurrentDeliveryPoints().size() > indexDP) {
            DeliveryPoint oldSelectedDP = controller.getWindow().getTextualView().getSelectedDeliveryPoint();
            if (oldSelectedDP != null){
                controller.getWindow().getGraphicalView().paintIntersection(oldSelectedDP, Color.BLUE, map);
            }
            DeliveryPoint dp = courier.getCurrentDeliveryPoints().get(indexDP);
            controller.getWindow().getTextualView().setSelectedDeliveryPoint(dp);
            System.out.println(dp);
            controller.getWindow().getGraphicalView().setSelectedIntersection(map.getIntersection(dp.getId()));
            controller.getWindow().getGraphicalView().paintIntersection(map.getIntersection(dp.getId()), Color.BROWN, map);
            controller.getWindow().allowNode("VALIDATE_DP", true);
            controller.getWindow().allowNode("REMOVE_DP", true);
        }
    }
}
