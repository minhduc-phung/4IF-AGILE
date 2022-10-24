/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import model.Courier;
import model.Intersection;
import model.Map;
import model.Segment;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import model.DeliveryPoint;
import model.User;
import org.w3c.dom.Document; 
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 *
 * @author nmngo
 */
public class Service {
    private User user = new User();
    
    public User getUser() {
        return this.user;
    }
    
    public Map loadMapFromXML(String XMLPath) throws ParserConfigurationException, IOException, SAXException {
        File XMLFile = new File(XMLPath);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbf.newDocumentBuilder();
        Document doc = dBuilder.parse(XMLFile);
        doc.getDocumentElement().normalize();

        HashMap<Long, Intersection> listIntersection = new HashMap<>();
        NodeList nodeList = doc.getElementsByTagName("intersection");
        for (int itr = 0; itr < nodeList.getLength(); itr++) {
            Node node = nodeList.item(itr);
            NamedNodeMap nodeMap = node.getAttributes();
            Long idInter = Long.parseLong(nodeMap.getNamedItem("id").getNodeValue());
            Double latitude = Double.parseDouble(nodeMap.getNamedItem("latitude").getNodeValue());
            Double longitude = Double.parseDouble(nodeMap.getNamedItem("longitude").getNodeValue());
            Intersection intersection = new Intersection(idInter, latitude, longitude);
            listIntersection.put(idInter, intersection);
        }

        Intersection warehouse = new Intersection(0L, 0.0, 0.0);
        NodeList nodeListWarehouse = doc.getElementsByTagName("warehouse");
        Node nodeWarehouse = nodeListWarehouse.item(0);
        NamedNodeMap nodeMapWarehouse = nodeWarehouse.getAttributes();
        Long idWarehouse = Long.parseLong(nodeMapWarehouse.getNamedItem("address").getNodeValue());
        for (Intersection intersection : listIntersection.values()) {
            if (Objects.equals(intersection.getId(), idWarehouse)) {
                warehouse = intersection;
            }
        }

        List<Segment> listSegment = new ArrayList<>();
        NodeList nodeListSeg = doc.getElementsByTagName("segment");
        for (int itr = 0; itr < nodeListSeg.getLength(); itr++) {
            Node node = nodeListSeg.item(itr);
            NamedNodeMap nodeMap = node.getAttributes();
            Long idOrigin = Long.parseLong(nodeMap.getNamedItem("origin").getNodeValue());
            Intersection origin = listIntersection.get(idOrigin);

            Long idDesti = Long.parseLong(nodeMap.getNamedItem("destination").getNodeValue());
            Intersection destination = listIntersection.get(idDesti);

            Double length = Double.parseDouble(nodeMap.getNamedItem("length").getNodeValue());
            String name = nodeMap.getNamedItem("name").getNodeValue();
            Segment segment = new Segment(origin, destination, length, name);
            listSegment.add(segment);
        }

        Map map = new Map(listIntersection, listSegment, warehouse);
        System.out.println(map.getListSegment().get(0));
        System.out.println(warehouse);
        return map;
    }
    
    public void saveDeliveryPointToFile(List<DeliveryPoint> listDP) throws ParserConfigurationException, SAXException, 
                                        IOException, TransformerConfigurationException, TransformerException, XPathExpressionException {
        File XMLFile = new File("saved_files/deliveryPoints.xml");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
        DocumentBuilder dBuilder = dbf.newDocumentBuilder();
        Document doc = dBuilder.parse(XMLFile);
        doc.getDocumentElement().normalize();
        
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        FileWriter writer = new FileWriter(XMLFile);
        StreamResult result = new StreamResult(writer);
        DOMSource source = new DOMSource(doc);
        
        Node nodePlanDates = doc.getElementsByTagName("planDates").item(0);
        XPath xPath = XPathFactory.newInstance().newXPath();
        
        for (DeliveryPoint dp : listDP) {
            String expression = "/planDates/planDate[@date='" + dp.getPlanDate() + "']";
            XPathExpression xPathExpression = xPath.compile(expression);
            Element nodePlanDate = (Element) xPathExpression.evaluate(doc, XPathConstants.NODE);
            
            try {
                nodePlanDates.appendChild(nodePlanDate);
            } catch (NullPointerException e) {
                nodePlanDate = doc.createElement("planDate");
                nodePlanDate.setAttribute("date", dp.getPlanDate().toString());
                nodePlanDates.appendChild(nodePlanDate);
            }
            
            expression = "/planDates/planDate[@date='"+dp.getPlanDate()+"']/deliveryPoint[@id='"+dp.getId().toString()+"' and @courierId='"+dp.getCourier().getId().toString()+"']";
            Element nodeDeliveryPoint = (Element) xPath.compile(expression).evaluate(doc, XPathConstants.NODE);
            try {
                nodePlanDate.appendChild(nodeDeliveryPoint);
            } catch (NullPointerException e) {
                nodeDeliveryPoint = doc.createElement("deliveryPoint");
                nodeDeliveryPoint.setAttribute("id", dp.getId().toString());
                nodeDeliveryPoint.setAttribute("courierId", dp.getCourier().getId().toString());
                nodePlanDate.appendChild(nodeDeliveryPoint);
            }
        }
        // Export the XMLFile
        transformer.transform(source, result);
    }
    
    public List<DeliveryPoint> restoreDeliveryPointFromXML(String XMLPathMap, String XMLPathDeliveryPoint, Date planDate) 
                                                    throws ParserConfigurationException, IOException, 
                                                    SAXException, XPathExpressionException {
        //precondition : Map is loaded and XMLfile of deliveryPoints exists
        Map map = this.loadMapFromXML(XMLPathMap);
        File XMLFileDP = new File(XMLPathDeliveryPoint);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
        DocumentBuilder dBuilder = dbf.newDocumentBuilder();
        Document doc = dBuilder.parse(XMLFileDP);
        doc.getDocumentElement().normalize();
        
        XPath xPath = XPathFactory.newInstance().newXPath();
        String expression = "planDates/planDate[@date='"+planDate.toString()+"']/deliveryPoint";
        NodeList nodeListDP = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
        
        List<DeliveryPoint> listDP = new ArrayList<>();
        for (int i = 0 ; i < nodeListDP.getLength() ; i++) {
            String idDP = nodeListDP.item(i).getAttributes().getNamedItem("id").getNodeValue();
            String courierId = nodeListDP.item(i).getAttributes().getNamedItem("courierId").getNodeValue();
            Intersection inter = map.getListIntersection().get(Long.parseLong(idDP));
            DeliveryPoint dp = new DeliveryPoint(planDate, inter.getId(), inter.getLatitude(), inter.getLongitude());
            dp.chooseCourier( this.user.getListCourier().get(Long.parseLong(courierId)) );
            listDP.add(dp);
        }
        return listDP;
    }
    
    public HashMap<Long, Courier> getListAvailableCourier(){
        return this.user.getListCourier();
    }
    
    public Courier selectCourier(Long idCourier){
        return this.user.getCourierById(idCourier);
    }
    public void enterDeliveryPoint(Map map, Long idIntersection, Date planDate, Long idCourier, Date TW ){
        Intersection i = map.getIntersection(idIntersection);
        DeliveryPoint DP = new DeliveryPoint(planDate,idIntersection,i.getLatitude(),i.getLongitude());
        Courier c = user.getCourierById(idCourier);
        DP.assignTimeWindow(TW);
        DP.chooseCourier(c);
        c.addDeliveryPoint(DP);
    }
    public void deleteDeliveryPoint(DeliveryPoint DP, List<DeliveryPoint> listDP){
        listDP.remove(DP);
    }
    
}