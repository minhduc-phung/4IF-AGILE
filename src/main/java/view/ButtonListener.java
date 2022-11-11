package view;

import controller.Controller;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import org.xml.sax.SAXException;
import xml.ExceptionXML;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

public class ButtonListener implements EventHandler<ActionEvent> {

    private Controller controller;

    public ButtonListener(Controller controller){
            this.controller = controller;
        }

    @Override
    public void handle(ActionEvent e) {
        switch (((Node) e.getSource()).getId()){
            case "LOAD_MAP":
                try {
                    controller.loadMapFromXML();
                } catch (ParserConfigurationException | IOException | SAXException | ExceptionXML ex) {
                    throw new RuntimeException(ex);
                }
                break;
            case "VALIDATE_DP":
                controller.enterDeliveryPoint(controller.getMap(), controller.getWindow().getGraphicalView().getSelectedIntersection().getId(), controller.getWindow().getInteractivePane().getSelectedCourierId(), controller.getWindow().getInteractivePane().getSelectedTimeWindow());
                break;
            case "REMOVE_DP": break;
            case "RESTORE_DP":
                // For ergonomy
                ((ComboBox<String>) controller.getWindow().lookup("#COURIER_BOX")).setValue(controller.getUser().getListCourierName()[0]);
                try {
                    controller.restoreDeliveryPointFromXML();
                } catch (ParserConfigurationException | IOException | SAXException | XPathExpressionException |
                         ExceptionXML ex) {
                    throw new RuntimeException(ex);
                }
                break;
            case "SAVE_DP":
                try {
                    controller.saveDeliveryPointToFile();
                } catch (XPathExpressionException | ParserConfigurationException | IOException | TransformerException |
                         SAXException | ExceptionXML ex) {
                    throw new RuntimeException(ex);
                }
                break;
            case "MODIFY_DP": break;
            case "GENERATE_PLAN": break;
            case "CALCULATE_TOUR": break;
        }


    }
}
