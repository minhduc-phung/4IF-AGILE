package view;

import controller.Controller;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import model.DeliveryPoint;
import org.xml.sax.SAXException;
import xml.ExceptionXML;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.text.ParseException;
import model.Courier;
import model.Intersection;

/**
 * this class handles the events happening after clicking on any button
 *
 */
public class ButtonListener implements EventHandler<ActionEvent> {

    private Controller controller;

    public ButtonListener(Controller controller) {
        this.controller = controller;
    }
    /**
     * this method handles the action to do after the incoming Event.
     *
     * @param e represents the incoming event after clicking on a button
     */
    @Override
    public void handle(ActionEvent e) {
        switch (((Node) e.getSource()).getId()) {
            case "LOAD_MAP":
                try {
                    controller.loadMapFromXML();
                } catch (ParserConfigurationException | IOException | SAXException | ExceptionXML ex) {
                    throw new RuntimeException(ex);
                }
                break;
            case "VALIDATE_DP":
                    controller.enterDeliveryPoint(controller.getMap(), controller.getWindow().getGraphicalView().getSelectedIntersection().getId(),
                            controller.getWindow().getInteractivePane().getSelectedCourierId(), controller.getWindow().getInteractivePane().getSelectedTimeWindow());
                break;
            case "REMOVE_DP":
                controller.removeDeliveryPoint(controller.getMap(), controller.getWindow().getTextualView().getSelectedDeliveryPoint(), controller.getWindow().getInteractivePane().getSelectedCourierId());
                break;
            case "RESTORE_DP":
                try {
                    controller.restoreDeliveryPointFromXML();
                } catch (ParserConfigurationException | IOException | SAXException | XPathExpressionException
                    | ExceptionXML ex) {
                    throw new RuntimeException(ex);
                }
                break;
            case "SAVE_DP":
                try {
                    controller.saveDeliveryPointToFile();
                } catch (XPathExpressionException | ParserConfigurationException | IOException | TransformerException
                    | SAXException | ExceptionXML ex) {
                    throw new RuntimeException(ex);
                }
                break;
            case "MODIFY_DP":
                controller.modifyTour();
                break;
            case "GENERATE_PLAN":
                Courier c = controller.getUser().getCourierById(controller.getWindow().getInteractivePane().getSelectedCourierId());
                try {
                    controller.generatePlan(c);
                } catch (ParserConfigurationException | SAXException | ExceptionXML
                        | IOException | TransformerException ex) {
                    throw new RuntimeException(ex);
                }
                break;
            case "CALCULATE_TOUR":
                try {
                    controller.calculateTour(controller.getUser().getCourierById(controller.getWindow().getInteractivePane().getSelectedCourierId()), controller.getMap().getWarehouse().getId());
                } catch (ParseException ex) {
                    throw new RuntimeException(ex);
                }
                break;
            case "ADD_DP_TO_TOUR":
                Courier courierToAdd = controller.getUser().getCourierById(controller.getWindow().getInteractivePane().getSelectedCourierId());
                Intersection intersectionToAdd = controller.getMap().getIntersection(controller.getWindow().getGraphicalView().getSelectedIntersection().getId());
                try {
                    controller.modifyTourEnterDP(courierToAdd, intersectionToAdd, controller.getWindow().getInteractivePane().getSelectedTimeWindow());
                } catch (ParseException ex) {
                    throw new RuntimeException(ex);
                }
                break;
            case "REMOVE_DP_FROM_TOUR":
                Courier courierToRemove = controller.getUser().getCourierById(controller.getWindow().getInteractivePane().getSelectedCourierId());
                DeliveryPoint deliveryPointToRemove = controller.getWindow().getTextualView().getSelectedDeliveryPoint();
                controller.modifyTourRemoveDP(courierToRemove, deliveryPointToRemove);
                break;

        }
    }
}
