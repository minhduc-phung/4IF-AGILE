/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import model.*;
import org.xml.sax.SAXException;
import view.Window;
import xml.ExceptionXML;
import xml.PlanTextWriter;
import xml.XMLmapDeserializer;

import static view.GraphicalView.IntersectionType.*;
import static view.GraphicalView.IntersectionType.ON_TIME;
import static xml.XMLmapDeserializer.addWarehouse;

public class TourCalculatedState implements State {
    @Override
    public void generateDeliveryPlanForCourier(Controller controller, Courier c) throws ParserConfigurationException, SAXException, ExceptionXML,
                                                                                                IOException, TransformerException{
        Map map = controller.map;
        PlanTextWriter.getInstance().save(map, c);
        controller.getWindow().setMessage("Delivery plan saved. To continue, please load a new map.");
        controller.getWindow().allowNode("LOAD_MAP", true);
        controller.getWindow().allowNode("GENERATE_PLAN", false);
        controller.setCurrentState(controller.planGeneratedState);
    }

    @Override
    public void selectCourier(Controller controller, Long idCourier) {
        controller.getWindow().getInteractivePane().setSelectedCourierId(idCourier);
        controller.getWindow().getTextualView().updateData(controller.getUser(), idCourier);
        controller.getWindow().getTextualView().clearSelection();
        controller.getWindow().getGraphicalView().updateMap(controller.getMap(), controller.user.getCourierById(idCourier));
        controller.getWindow().setMessage("Courier " + controller.user.getCourierById(idCourier).getName() + " selected.");
        controller.getWindow().getGraphicalView().clearSelection();
        controller.setCurrentState(controller.courierChosenState);
        controller.getWindow().allowNode("VALIDATE_DP", false);
        controller.getWindow().allowNode("REMOVE_DP", false);
        controller.getWindow().resetLateDeliveryNumber();
    }

    @Override
    public void modifyTour(Controller controller) {
        controller.setCurrentState(controller.tourModifiedState);
        controller.getWindow().getInteractivePane().hideButton("MODIFY_DP");
        controller.getWindow().getInteractivePane().hideButton("REMOVE_DP");
        controller.getWindow().getInteractivePane().hideButton("VALIDATE_DP");
        controller.getWindow().getInteractivePane().showButton("ADD_DP_TO_TOUR");
        controller.getWindow().getInteractivePane().showButton("REMOVE_DP_FROM_TOUR");
        controller.getWindow().allowNode("ADD_DP_TO_TOUR", false);
        controller.getWindow().allowNode("REMOVE_DP_FROM_TOUR", false);
    }

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
    public void mouseClickedOnTable(Controller controller, int indexDP){
        Map map = controller.map;
        User user = controller.user;
        Courier courier = user.getCourierById(controller.getWindow().getInteractivePane().getSelectedCourierId());
        if (courier.getCurrentDeliveryPoints().size() > indexDP) {
            DeliveryPoint oldSelectedDP = controller.getWindow().getTextualView().getSelectedDeliveryPoint();
            if (oldSelectedDP != null){
                if (oldSelectedDP.getEstimatedDeliveryTime().getHours() > oldSelectedDP.getTimeWindow()) {
                    controller.getWindow().getGraphicalView().paintIntersection(oldSelectedDP, LATE);
                } else {
                    controller.getWindow().getGraphicalView().paintIntersection(oldSelectedDP, ON_TIME);
                }
            }
            DeliveryPoint dp = courier.getCurrentDeliveryPoints().get(indexDP);
            controller.getWindow().getTextualView().setSelectedDeliveryPoint(dp);
            System.out.println(dp);
            controller.getWindow().getGraphicalView().setSelectedIntersection(map.getIntersection(dp.getId()));
            controller.getWindow().getGraphicalView().paintIntersection(map.getIntersection(dp.getId()), SELECTED);
        }
    }
}
