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
 *
 * @author bbbbb
 */
public class TourCalculatedState implements State {
    @Override
    public void generateDeliveryPlanForCourier(Controller controller, Courier c) throws ParserConfigurationException, SAXException, ExceptionXML,
                                                                                                IOException, TransformerException{
        Map map = controller.map;
        XMLPlanSerializer.getInstance().save(map, c);
        controller.getWindow().setMessage("Delivery plan saved. To continue, please load a new map.");
        controller.getWindow().allowNode("LOAD_MAP", true);
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
        controller.getWindow().getInteractivePane().showButton("MODIFY_ENTER_DP");
        controller.getWindow().getInteractivePane().hideButton("MODIFY_DP");
        controller.getWindow().getInteractivePane().hideButton("REMOVE_DP");
        controller.getWindow().getInteractivePane().hideButton("VALIDATE_DP");
    }
}
