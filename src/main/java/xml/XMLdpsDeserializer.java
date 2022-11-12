package xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import model.Courier;
import model.DeliveryPoint;
import model.Intersection;
import model.Map;
import model.User;

public class XMLdpsDeserializer {
	/**
	 * Open an XML file and create plan from this file
	 * @param map the map to create from the file
         * @param user
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ExceptionXML
         * @throws javax.xml.xpath.XPathExpressionException
	 */
    public static User loadDPList(Map map, User user) throws ParserConfigurationException, SAXException, IOException, ExceptionXML, XPathExpressionException {
        File xml = XMLfileOpener.getInstance().open(true);
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = docBuilder.parse(xml);
        Element root = document.getDocumentElement();
        DeliveryPoint dpWarehouse = new DeliveryPoint(map.getWarehouse().getId(), map.getWarehouse().getLatitude(), map.getWarehouse().getLongitude());
        for (Long idCourier: user.getListCourier().keySet()){
            Courier courier = user.getCourierById(idCourier);
            courier.addDeliveryPoint(dpWarehouse);
        }
        
        if (root.getNodeName().equals("maps")) {
            XPath xPath = XPathFactory.newInstance().newXPath();
            String expression = "maps/map[@src='"+map.getMapName()+"']/courier";
            NodeList nodeListCourier = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
            
            if (nodeListCourier.getLength() >0) {
                user = buildFromDOMXML(nodeListCourier, map, user, document);
            }else{
                throw new ExceptionXML("No couriers found");
            }
        } else {
            throw new ExceptionXML("Wrong format");
        }
        
        return user;
    }

    private static User buildFromDOMXML(NodeList nodeListCourier, Map map, User user, Document document) throws ExceptionXML, NumberFormatException, XPathExpressionException{            
        for (int i = 0 ; i < nodeListCourier.getLength() ; i++) {
            Element eltCourier = (Element) nodeListCourier.item(i);
            Long idCourier = Long.valueOf(eltCourier.getAttribute("id")); 
            
            XPath xPath = XPathFactory.newInstance().newXPath();
            String expression = "maps/map[@src='"+map.getMapName()+"']/courier[@id='"+idCourier.toString()+"']/deliveryPoint";
            NodeList nodeListDP = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
            
            Courier c = user.getCourierById(idCourier);            
            for (int j =0; j < nodeListDP.getLength(); j++) {
                Element eltDP = (Element) nodeListDP.item(j); 
                
                c = addDeliveryPointToCourier (c,eltDP,map);
                user.getListCourier().replace(idCourier, c);
            }           
        }        
        return user;
    }
    
    private static Courier addDeliveryPointToCourier (Courier c, Element eltDP, Map map) {
        String idDP = eltDP.getAttribute("id");
        
        
        Intersection inter = map.getListIntersection().get(Long.valueOf(idDP));
        DeliveryPoint dp = new DeliveryPoint(inter.getId(), inter.getLatitude(), inter.getLongitude());
        dp.assignTimeWindow(Integer.parseInt(eltDP.getAttribute("timeWindow")));
        dp.chooseCourier(c);
        c.addDeliveryPoint(dp);
        
        return c;
    }

}
