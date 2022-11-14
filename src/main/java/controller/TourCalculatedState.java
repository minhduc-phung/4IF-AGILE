/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import model.Courier;
import model.Map;
import org.xml.sax.SAXException;
import xml.ExceptionXML;
import xml.XMLPlanSerializer;

/**
 *
 * @author bbbbb
 */
public class TourCalculatedState implements State {
    @Override
    public void generatedDeliveryPlanForCourier(Controller controller, Courier c) throws ParserConfigurationException, SAXException, ExceptionXML, 
                                                                                IOException, TransformerConfigurationException, TransformerException{
        Map map = controller.map;
        XMLPlanSerializer.getInstance().save(map, c);
        controller.getWindow().setMessage("Delivery plans saved.");
        controller.setCurrentState(controller.planGeneratedState);
    }
}
