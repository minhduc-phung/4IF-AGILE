package xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import model.Intersection;
import model.Map;
import model.Segment;

public class XMLmapDeserializer {
	/**
	 * Open an XML file and create plan from this file
	 * @param map the map to create from the file
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ExceptionXML
	 */
    public static Map load(Map map) throws ParserConfigurationException, SAXException, IOException, ExceptionXML {
        File xml = XMLfileOpener.getInstance().open(true);
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = docBuilder.parse(xml);
       
        Element root = document.getDocumentElement();
        if (root.getNodeName().equals("map")) {
            map = buildFromDOMXML(root, map, xml.getName());
        } else {
            throw new ExceptionXML("The chosen file does not have the correct format.");
        }
        
        return map;
    }

    private static Map buildFromDOMXML(Element noeudDOMRacine, Map map, String mapName) throws ExceptionXML, NumberFormatException{               
        HashMap<Long, Intersection> listIntersection = new HashMap<>();
        
        NodeList nodeList = noeudDOMRacine.getElementsByTagName("intersection");
        for (int itr = 0; itr < nodeList.getLength(); itr++) {
            createIntersection((Element) nodeList.item(itr), listIntersection);
        }

        Intersection warehouse = new Intersection(0L, 0.0, 0.0);
        NodeList nodeListWarehouse = noeudDOMRacine.getElementsByTagName("warehouse");        
        warehouse = addWarehouse(warehouse, (Element) nodeListWarehouse.item(0), listIntersection);
        
        List<Segment> listSegment = new ArrayList<>();
        NodeList nodeListSeg = noeudDOMRacine.getElementsByTagName("segment");
        for (int itr = 0; itr < nodeListSeg.getLength(); itr++) {            
            createSegment((Element) nodeListSeg.item(itr),listSegment, listIntersection);
        }
        map = new Map(mapName, listIntersection, listSegment, warehouse);
        return map;
    }
    
    private static void createIntersection(Element elt, HashMap<Long, Intersection> listIntersection) throws ExceptionXML {
        Long idInter = Long.parseLong(elt.getAttribute("id"));
        Double latitude = Double.parseDouble(elt.getAttribute("latitude"));
        Double longitude = Double.parseDouble(elt.getAttribute("longitude"));
        Intersection intersection = new Intersection(idInter, latitude, longitude);
        
        listIntersection.put(intersection.getId(),intersection);
    }
    
    private static void createSegment(Element elt, List<Segment> listSegment, HashMap<Long, Intersection> listIntersection) throws ExceptionXML {
        Long idOrigin = Long.parseLong(elt.getAttribute("origin"));
        Intersection origin = listIntersection.get(idOrigin);

        Long idDesti = Long.parseLong(elt.getAttribute("destination"));
        Intersection destination = listIntersection.get(idDesti);
        
        Double length = Double.parseDouble(elt.getAttribute("length"));
        
        origin.addTravelTimeToNextIntersection(idDesti, length*60.0/(15.0*1000.0));
            
        String name = elt.getAttribute("name");
        Segment segment = new Segment(origin, destination, length, name);
          
        listSegment.add(segment);
    }
    
    public static Intersection addWarehouse (Intersection warehouse, Element eltWarehouse, 
            HashMap<Long, Intersection> listIntersection) {
        
        Long idWarehouse = Long.parseLong(eltWarehouse.getAttribute("address"));
        for (Intersection intersection : listIntersection.values()) {
            if (Objects.equals(intersection.getId(), idWarehouse)) {
                warehouse = intersection;
            }
        }
        
        return warehouse;

    }
}

