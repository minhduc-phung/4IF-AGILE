/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package xml;

import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;
import model.Courier;
import model.DeliveryPoint;
import model.Map;
import model.Segment;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


/**
 *
 * @author PQV
 */
public class XMLPlanSerializer {
    private Document document;
    private static XMLPlanSerializer instance = null;
    private XMLPlanSerializer(){}
    public static XMLPlanSerializer getInstance() {
        if (instance == null) {
            instance = new XMLPlanSerializer();
        }
        return instance;
    }
        /**
     * Open an XML file and write an XML description of the plan in it 
     * @param listDP
     * @param user
     * @param plan the plan to serialise
     * @throws ParserConfigurationException
     * @throws TransformerFactoryConfigurationError
     * @throws TransformerException
     * @throws ExceptionXML
     */
    public void save(Map map, Courier courier) throws ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException, ExceptionXML, SAXException, IOException{
        File xml = XMLfileOpener.getInstance().open(false);
        StreamResult result = new StreamResult(xml);
        document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        
        Element root = document.getDocumentElement();
        if (root == null) {
            createCourierElt(map, courier);
            DOMSource source = new DOMSource(document);
            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            //xformer.setOutputProperty(OutputKeys.INDENT, "yes");
            xformer.transform(source, result);
        } else {
            throw new ExceptionXML("Not an empty file");
        }
                
    }

    private void createCourierElt(Map map, Courier courier){
        Element eltCourier = createCourierElement(courier, map);
        document.appendChild(eltCourier);
        
        for (int i =0; i < courier.getCurrentDeliveryPoints().size(); i++) {
            DeliveryPoint dp = courier.getCurrentDeliveryPoints().get(i);
            Element eltDP = createDeliveryPointElement(dp);
            HashMap<Long, List<Segment>> courierTour = courier.getCurrentTour().getTourRoute();
            
            for (Long idDP : courierTour.keySet()) {
                List<Segment> listSegment = courierTour.get(idDP);
                for (Segment segment : listSegment) {
                    Element eltSeg = createSegmentElement(segment);
                    eltDP.appendChild(eltSeg);
                }
            }
            
            eltCourier.appendChild(eltDP);
        }
    }

    private void createAttribute(Element root, String name, String value){
    	Attr attribut = document.createAttribute(name);
    	root.setAttributeNode(attribut);
    	attribut.setValue(value);
    }
    
    private Element createCourierElement(Courier c, Map map) {
        Element eltCourier = document.createElement("courier");
        createAttribute(eltCourier, "id", c.getId().toString());
        createAttribute(eltCourier, "name", c.getName());
        createAttribute(eltCourier, "map", map.getMapName());
        return eltCourier;
    }
    
    private Element createDeliveryPointElement(DeliveryPoint dp) {
        Element eltDeliveryPoint = document.createElement("deliveryPoint");
        createAttribute(eltDeliveryPoint, "id", dp.getId().toString());
        createAttribute(eltDeliveryPoint,"estimatedDeliveryTime", dp.getEstimatedDeliveryTimeString());
        return eltDeliveryPoint;
    }

    private Element createSegmentElement (Segment segment) {
        Element eltSegment = document.createElement("segment");
        createAttribute(eltSegment, "name", segment.getName());
        createAttribute(eltSegment, "from", segment.getOrigin().getId().toString());
        createAttribute(eltSegment, "to", segment.getDestination().getId().toString());
        return eltSegment;
    }    
    
}
