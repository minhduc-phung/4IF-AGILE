/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
import model.CompleteGraph;
import model.Courier;
import model.DeliveryPoint;
import model.Graph;
import model.Intersection;
import model.Map;
import model.TSP;
import model.TSP1;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author bbbbb
 */
public class DPEnteredState implements State {
    @Override
    public Double calculateTour(Controller controller, Courier c, Long idWarehouse) {
        Graph g = new CompleteGraph(c, idWarehouse);
        TSP tsp = new TSP1();
        tsp.searchSolution(20000, g);
        controller.setCurrentState(controller.tourCalculatedState);
        return tsp.getSolutionCost();
    }
    
    @Override
    public void enterDeliveryPoint(Controller controller, Map map, Long idIntersection, Long idCourier, Integer timeWindow) {
        Intersection i = map.getIntersection(idIntersection);
        if (idIntersection.equals(map.getWarehouse().getId())) {
            return;
        }
        DeliveryPoint dp = new DeliveryPoint(idIntersection,i.getLatitude(),i.getLongitude());
        Courier c = controller.user.getCourierById(idCourier);        
        dp.assignTimeWindow(timeWindow);
        dp.chooseCourier(c);
        c.addDeliveryPoint(dp);
        c.addPositionIntersection(idIntersection);
        if (!c.getShortestPathBetweenDPs().isEmpty()) {
            controller.addShortestPathBetweenDP(map, c, dp);
        } else {
            c.getShortestPathBetweenDPs().put(dp.getId(), new HashMap<>());
        }
        controller.setCurrentState(controller.dpEnteredState);
    }
    
    @Override
    public void removeDeliveryPoint(Controller controller, Map map, DeliveryPoint dp, Long idCourier){
        if (dp.getId().equals(map.getWarehouse().getId())) {
            return;
        }       
        Courier c = controller.user.getCourierById(idCourier);
        dp.chooseCourier(null);
        c.removeDeliveryPoint(dp);
        c.getPositionIntersection().remove(dp.getId());
        controller.removeShortestPathBetweenDP(c, dp);
        controller.setCurrentState(controller.dpRemovedState);
    }
    
/*    @Override
    public void saveDeliveryPointToFile(Controller controller, List<DeliveryPoint> listDP) throws ParserConfigurationException, SAXException, 
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
        controller.setCurrentState(controller.dpSavedState);
    }*/
}
