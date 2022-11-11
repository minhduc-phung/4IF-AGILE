/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.util.HashMap;
import javax.xml.parsers.ParserConfigurationException;
import model.Courier;
import model.DeliveryPoint;
import model.Intersection;
import model.User;
import org.xml.sax.SAXException;
import view.Window;
import xml.ExceptionXML;
import xml.XMLmapDeserializer;

/**
 *
 * @author bbbbb
 */
public class PlanGeneratedState implements State {
    @Override
    public void loadMapFromXML(Controller controller, Window window) throws ExceptionXML, ParserConfigurationException, SAXException, IOException {
        controller.map = XMLmapDeserializer.load(controller.map);
        controller.user = new User();
        Intersection warehouse = controller.getMap().getWarehouse();
        addWarehouse(warehouse, controller.user);
        controller.setCurrentState(controller.mapLoadedState);
        window.getGraphicalView().drawMap(controller.getMap());
        window.allowNode("COURIER_BOX", true);
        window.allowNode("TW_BOX", true);
        window.getInteractivePane().resetComboBoxes();
        window.setMessage("Please choose a courier and a time-window to start adding delivery points.");

    }
    
    private void addWarehouse (Intersection warehouse, User user) {
        DeliveryPoint dpWarehouse = new DeliveryPoint(warehouse.getId(), warehouse.getLatitude(), warehouse.getLongitude());
        for (Long key : user.getListCourier().keySet()) {
            Courier c = user.getListCourier().get(key);
            dpWarehouse.chooseCourier(c);
            c.addDeliveryPoint(dpWarehouse);
            
            c.addPositionIntersection(warehouse.getId());
            HashMap<Long, Double> nestedMap = new HashMap<>();
            nestedMap.put(warehouse.getId(), Double.valueOf("0.0"));
            c.getShortestPathBetweenDPs().put(warehouse.getId(), nestedMap);
            user.getListCourier().replace(key, c);
        }
    }
    
    /*@Override
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
    
    @Override
    public void selectCourier(Controller controller, Long idCourier) {
        controller.getWindow().getInteractivePane().setSelectedCourierId(idCourier);
        controller.getWindow().getGraphicalView().updateMap(controller.getMap(), controller.user.getCourierById(idCourier));
        controller.setCurrentState(controller.courierChosenState);
    }
}
