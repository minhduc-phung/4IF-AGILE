/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import model.*;
import org.xml.sax.SAXException;
import xml.ExceptionXML;
import xml.PlanTextWriter;

import static view.GraphicalView.IntersectionType.*;
import static view.GraphicalView.IntersectionType.ON_TIME;

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
