/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.util.HashMap;
import javax.xml.parsers.ParserConfigurationException;
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
import xml.XMLmapDeserializer;

/**
 * This class is for the state where a map is already loaded.
 * Its methods are executed in the Controller class when the current state is DPEnteredState.
 */
public class MapLoadedState implements State {
    /**
     * this method allows the user to load a map from an XML file
     * @param controller
     * @param window
     * @throws xml.ExceptionXML
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
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
     * this method allows the user to select a courier from those existent
     * @param controller
     * @param idCourier the id of the courier to select
     * @see model.Courier
     */
    
    @Override
    public void selectCourier(Controller controller, Long idCourier) {
        controller.getWindow().getInteractivePane().setSelectedCourierId(idCourier);
        controller.setCurrentState(controller.courierChosenState);
        controller.getWindow().getTextualView().updateData(controller.getUser(), idCourier);
        controller.getWindow().setMessage("Please choose a time-window to start adding delivery points.");
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
}
