/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.Service;
import model.Map;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import model.Courier;
import model.DeliveryPoint;
import org.xml.sax.SAXException;

/**
 *
 * @author nmngo
 */
public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, 
                                SAXException, ParseException, TransformerException, 
                                TransformerConfigurationException, XPathExpressionException {
        //testLoadMap();
        //testSaveDeliveryPoints();
        testSaveDeliveryPoints();
    }
    
    public static void testLoadMap() throws ParserConfigurationException, IOException, SAXException {
        Service service = new Service();
        Map map = service.loadMapFromXML("maps/mediumMap.xml");
    }
    
    public static void testSaveDeliveryPoints() throws ParserConfigurationException, SAXException, 
                            IOException, TransformerException, ParseException, TransformerConfigurationException, 
                            XPathExpressionException {
        Service service = new Service();
        Courier c = new Courier(Long.parseLong("3"), "Minh");
        
        // respect this format
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd hh:mm:ss z yyyy");
        Date planDate = sdf.parse("Tue Oct 25 00:00:00 CEST 2022");
        DeliveryPoint dp = new DeliveryPoint(planDate, Long.parseLong("1850080438"),
                                            Double.parseDouble("45.754265"), Double.parseDouble("4.886816"));
        // persist deliveryPoint and courier
        dp.chooseCourier(c);
        c.addDeliveryPoint(dp);
        // call service
        service.saveDeliveryPointToFile(c);
    }
}
