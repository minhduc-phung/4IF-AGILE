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

public class ButtonListener implements EventHandler<ActionEvent> {

    private Controller controller;

    public ButtonListener(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void handle(ActionEvent e) {
        switch (((Node) e.getSource()).getId()) {
            case "LOAD_MAP":
                try {
                    controller.loadMapFromXML();
                } catch (ParserConfigurationException | IOException | SAXException | ExceptionXML ex) {
                    controller.getWindow().setMessage(ex.getMessage());
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
                    controller.getWindow().setMessage("Error while restoring delivery points from XML file: " + ex.getMessage());
                }
                break;
            case "SAVE_DP":
                try {
                    controller.saveDeliveryPointToFile();
                } catch (XPathExpressionException | ParserConfigurationException | IOException | TransformerException
                    | SAXException | ExceptionXML ex) {
                    controller.getWindow().setMessage("Error while saving the delivery points to a file: " + ex.getMessage());
                }
                break;
            case "MODIFY_DP":
                controller.modifyTour();
                break;
            case "GENERATE_PLAN":
                Courier c = controller.getUser().getCourierById(controller.getWindow().getInteractivePane().getSelectedCourierId());
                try {
                    controller.generatePlan(c);
                } catch (ParserConfigurationException | TransformerException | IOException | ExceptionXML |
                         SAXException ex) {
                    controller.getWindow().setMessage("Error while generating the plan: " + ex.getMessage());
                }
                break;
            case "CALCULATE_TOUR":
                try {
                    controller.calculateTour(controller.getUser().getCourierById(controller.getWindow().getInteractivePane().getSelectedCourierId()), controller.getMap().getWarehouse().getId());
                } catch (ParseException ex) {
                    controller.getWindow().setMessage("Error while calculating the tour: " + ex.getMessage());
                }
                break;
            case "ADD_DP_TO_TOUR":
                Courier courierToAdd = controller.getUser().getCourierById(controller.getWindow().getInteractivePane().getSelectedCourierId());
                Intersection intersectionToAdd = controller.getMap().getIntersection(controller.getWindow().getGraphicalView().getSelectedIntersection().getId());
                try {
                    controller.modifyTourEnterDP(courierToAdd, intersectionToAdd, controller.getWindow().getInteractivePane().getSelectedTimeWindow());
                } catch (ParseException ex) {
                    controller.getWindow().setMessage("Error while adding the delivery point to the tour: " + ex.getMessage());
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
