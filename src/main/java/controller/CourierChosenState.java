/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import model.Courier;
import model.DeliveryPoint;
import model.Intersection;
import model.Map;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author bbbbb
 */
public class CourierChosenState implements State {
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
    public List<DeliveryPoint> restoreDeliveryPointFromXML(Controller controller, String XMLPathMap, String XMLPathDeliveryPoint, Date planDate) 
                                                    throws ParserConfigurationException, IOException, 
                                                    SAXException, XPathExpressionException {
        //precondition : Map is loaded and XMLfile of deliveryPoints exists
        Map map = this.loadMapFromXML(controller, XMLPathMap);
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
            DeliveryPoint dp = new DeliveryPoint(inter.getId(), inter.getLatitude(), inter.getLongitude());
            dp.chooseCourier( controller.user.getListCourier().get(Long.parseLong(courierId)) );
            listDP.add(dp);
        }
        controller.setCurrentState(controller.dpRestoredState);
        return listDP;
    }
}
