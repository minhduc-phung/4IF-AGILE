/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import model.Courier;
import model.DeliveryPoint;
import model.Intersection;
import model.Map;
import xml.XMLPlanSerializer;
import org.xml.sax.SAXException;
import xml.ExceptionXML;
import xml.PlanTextWriter;

/**
 * This class is for the state where the tour is already calculated.
 * Its methods are executed in the Controller class when the current state is DPEnteredState.
 */
public class TourCalculatedState implements State {
    /**
     * this method generates a delivery plan for a chosen courier
     * @param controller
     * @param c the courier chosen
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws ExceptionXML
     * @throws IOException
     * @throws TransformerException
     */
    @Override
    public void generateDeliveryPlanForCourier(Controller controller, Courier c) throws ParserConfigurationException, SAXException, ExceptionXML,
                                                                                                IOException, TransformerException{
        Map map = controller.map;
        XMLPlanSerializer.getInstance().save(map, c);
        controller.getWindow().setMessage("Delivery plan saved. To continue, please load a new map.");
        controller.getWindow().allowNode("LOAD_MAP", true);
        controller.setCurrentState(controller.planGeneratedState);
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
        controller.getWindow().getGraphicalView().updateMap(controller.getMap(), controller.user.getCourierById(idCourier));
        controller.getWindow().setMessage("Courier " + controller.user.getCourierById(idCourier).getName() + " selected.");
        controller.getWindow().getGraphicalView().clearSelection();
        controller.setCurrentState(controller.courierChosenState);
        controller.getWindow().allowNode("VALIDATE_DP", false);
        controller.getWindow().allowNode("REMOVE_DP", false);
        controller.getWindow().resetLateDeliveryNumber();
    }

    /**
     * this method allows the user to modify the tour by adding a delivery point or removing it
     * @param controller
     */
    @Override
    public void modifyTour(Controller controller) {
        controller.setCurrentState(controller.tourModifiedState);
        controller.getWindow().getInteractivePane().hideButton("MODIFY_DP");
        controller.getWindow().getInteractivePane().hideButton("REMOVE_DP");
        controller.getWindow().getInteractivePane().hideButton("VALIDATE_DP");
        controller.getWindow().getInteractivePane().showButton("ADD_DP_TO_TOUR");
        controller.getWindow().getInteractivePane().showButton("REMOVE_DP_FROM_TOUR");
    }
}
