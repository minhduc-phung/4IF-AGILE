/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import controller.Service;
import java.io.FileWriter;
import model.Map;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
        //testRestoreDeliveryPoints();
        testDijkstra();
        //testEnterDeliveryPoint();
        //testRemoveDeliveryPoint();
    }
    
    public static void testLoadMap() throws ParserConfigurationException, IOException, SAXException {
        Service service = new Service();
        Map map = service.loadMapFromXML("maps/mediumMap.xml");
    }
    
    public static void testSaveDeliveryPoints() throws ParserConfigurationException, SAXException, 
                            IOException, TransformerException, ParseException, TransformerConfigurationException, 
                            XPathExpressionException {
        Service service = new Service();
        Courier c = service.getUser().getListCourier().get(Long.parseLong("4"));
    
        // respect this format
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd hh:mm:ss z yyyy", Locale.ENGLISH);
        Date planDate = sdf.parse("Mon Oct 24 00:00:00 CEST 2022");
        DeliveryPoint dp = new DeliveryPoint(planDate, Long.parseLong("1850080438"),
                                            Double.parseDouble("45.754265"), Double.parseDouble("4.886816"));
        // persist deliveryPoint and courier
        dp.chooseCourier(c);
        c.addDeliveryPoint(dp);
        // call service
        service.saveDeliveryPointToFile(c.getCurrentDeliveryPoints());
    }
    
    public static void testRestoreDeliveryPoints() throws ParserConfigurationException, 
                        IOException, XPathExpressionException, SAXException, ParseException {
        Service service = new Service();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd hh:mm:ss z yyyy");
        Date planDate = sdf.parse("Sun Oct 23 00:00:00 CEST 2022");
        List<DeliveryPoint> listDP = service.restoreDeliveryPointFromXML("maps/mediumMap.xml", 
                                "saved_files/deliveryPoints.xml", planDate);
        for (DeliveryPoint dp : listDP) {
            System.out.println(dp);
        }
    }
    
    public static void testDijkstra() throws ParserConfigurationException, IOException, SAXException {
        Service service = new Service();
        Map map = service.loadMapFromXML("maps/mediumMap.xml");
        System.out.println( service.dijkstra(map, Long.parseLong("60901982"), Long.parseLong("143370")) );
    }
    
    public static void testEnterDeliveryPoint() throws ParserConfigurationException, IOException, SAXException, ParseException {
        Service service = new Service();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd hh:mm:ss z yyyy");
        Date planDate = sdf.parse("Sun Oct 23 00:00:00 CEST 2022");
        Map map = service.loadMapFromXML("maps/mediumMap.xml");
        Long[] listIdInter = {Long.parseLong("2129259178"), Long.parseLong("2129259180"), Long.parseLong("239601996"),
                                Long.parseLong("21703589"), Long.parseLong("60901982")};
        Courier c = service.getUser().getCourierById(Long.parseLong("1"));

        for (Long idInter : listIdInter) {
            service.enterDeliveryPoint(map, idInter, planDate, c.getId(), null);
        }
        
        // shortest path of courier 1
        for (Long key : c.getShortestPathBetweenDPs().keySet()) {
            for (Long key2 : c.getShortestPathBetweenDPs().get(key).keySet()) {
                System.out.print(c.getShortestPathBetweenDPs().get(key).get(key2) + "  ");
            }
            System.out.println();
        }
    }
    
    public static void testRemoveDeliveryPoint() throws ParserConfigurationException, IOException, SAXException, ParseException {
        Service service = new Service();
        Map map = service.loadMapFromXML("maps/mediumMap.xml");
        Courier c = service.getUser().getCourierById(Long.parseLong("1"));
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd hh:mm:ss z yyyy");
        Date planDate = sdf.parse("Sun Oct 23 00:00:00 CEST 2022");
        // add 20 points to listDP
        Integer i = 0;
        for (Long idInter : map.getListIntersection().keySet()) {
            if (i < 20) {
                DeliveryPoint dp = new DeliveryPoint(planDate, idInter, map.getIntersection(idInter).getLatitude(), map.getIntersection(idInter).getLongitude());    
                service.enterDeliveryPoint(map, idInter, planDate, c.getId(), null);
                i++;
            } else break;
        }   
        
        // Delete a point
        DeliveryPoint aDP = new DeliveryPoint(planDate, Long.parseLong("25303831"), Double.parseDouble("25303831"), 
                            Double.parseDouble("4.87572"));
        service.removeDeliveryPoint(map, aDP, c);
        for (Long key : c.getShortestPathBetweenDPs().keySet()) {
            System.out.println(key.toString() + c.getShortestPathBetweenDPs().get(key));
        }
    }
}
