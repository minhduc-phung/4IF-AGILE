/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import model.Courier;
import model.Map;
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
        PlanTextWriter.getInstance().save(map, c);
        controller.getWindow().setMessage("Delivery plans saved.");
        controller.setCurrentState(controller.planGeneratedState);
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
