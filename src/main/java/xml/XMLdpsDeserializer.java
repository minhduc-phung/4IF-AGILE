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

import model.Circle;
import model.Courier;
import model.DeliveryPoint;
import model.Intersection;
import model.Plan;
import model.Point;
import model.PointFactory;
import model.Rectangle;
import model.Map;
import model.Segment;
import model.User;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class XMLdpsDeserializer {
	/**
	 * Open an XML file and create plan from this file
	 * @param map the map to create from the file
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ExceptionXML
	 */
    public static void load(Map map, User user) throws ParserConfigurationException, SAXException, IOException, ExceptionXML {
        File xml = XMLfileOpener.getInstance().open(true);
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = docBuilder.parse(xml);
        Element root = document.getDocumentElement();
        if (root.getNodeName().equals("maps")) {
            buildFromDOMXML(root, map, user);
        } else {
            throw new ExceptionXML("Wrong format");
        }
    }

    private static void buildFromDOMXML(Element noeudDOMRacine, Map map, User user) throws ExceptionXML, NumberFormatException{
    	int height = Integer.parseInt(noeudDOMRacine.getAttribute("height"));
        if (height <= 0)
        	throw new ExceptionXML("Error when reading file: The plan height must be positive");
        int width = Integer.parseInt(noeudDOMRacine.getAttribute("width"));
        if (width <= 0)
        	throw new ExceptionXML("Error when reading file: The plan width must be positive");
       	plan.reset(width,height);
       	NodeList circleList = noeudDOMRacine.getElementsByTagName("circle");
       	for (int i = 0; i < circleList.getLength(); i++) {
        	plan.add(createCircle((Element) circleList.item(i)));
       	}
       	NodeList rectangleList = noeudDOMRacine.getElementsByTagName("rectangle");
       	for (int i = 0; i < rectangleList.getLength(); i++) {
          	plan.add(createRectangle((Element) rectangleList.item(i)));
       	}
        
               
        HashMap<Long, Intersection> listIntersection = new HashMap<>();
        
        NodeList nodeList = noeudDOMRacine.getElementsByTagName("intersection");
        for (int itr = 0; itr < nodeList.getLength(); itr++) {
            createIntersection((Element) nodeList.item(itr), listIntersection);
        }

        Intersection warehouse = new Intersection(0L, 0.0, 0.0);
        NodeList nodeListWarehouse = noeudDOMRacine.getElementsByTagName("warehouse");        
        addWarehouse(warehouse, (Element) nodeListWarehouse.item(0), listIntersection, user);
        
        List<Segment> listSegment = new ArrayList<>();
        NodeList nodeListSeg = noeudDOMRacine.getElementsByTagName("segment");
        for (int itr = 0; itr < nodeListSeg.getLength(); itr++) {            
            createSegment((Element) nodeListSeg.item(itr),listSegment, listIntersection);
        }

        map = new Map(listIntersection, listSegment, warehouse);
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
    
    public static void addWarehouse (Intersection warehouse, Element eltWarehouse, 
            HashMap<Long, Intersection> listIntersection, User user) {
        
        Long idWarehouse = Long.parseLong(eltWarehouse.getAttribute("address"));
        for (Intersection intersection : listIntersection.values()) {
            if (Objects.equals(intersection.getId(), idWarehouse)) {
                warehouse = intersection;
            }
        }
        
        DeliveryPoint dpWarehouse = new DeliveryPoint(warehouse.getId(), 
                            warehouse.getLatitude(), warehouse.getLongitude());
        for (Long key : user.getListCourier().keySet()) {
            Courier c = user.getListCourier().get(key);
            dpWarehouse.chooseCourier(c);
            c.addDeliveryPoint(dpWarehouse);
            c.addPositionIntersection(idWarehouse);
            HashMap<Long, Double> nestedMap = new HashMap<>();
            nestedMap.put(warehouse.getId(), Double.valueOf("0.0"));
            c.getShortestPathBetweenDPs().put(warehouse.getId(), nestedMap);
            user.getListCourier().replace(key, c);
        }
    }
    
    private static Circle createCircle(Element elt) throws ExceptionXML{
   		int x = Integer.parseInt(elt.getAttribute("x"));
   		int y = Integer.parseInt(elt.getAttribute("y"));
   		Point p = PointFactory.createPoint(x, y);
   		if (p == null)
   			throw new ExceptionXML("Error when reading file: Point coordinates must belong to the plan");
   		int radius = Integer.parseInt(elt.getAttribute("radius"));
   		if (radius <= 0)
   			throw new ExceptionXML("Error when reading file: Radius must be positive");
   		return new Circle(p, radius);
    }
    
    private static Rectangle createRectangle(Element elt) throws ExceptionXML{
   		int x = Integer.parseInt(elt.getAttribute("x"));
   		int y = Integer.parseInt(elt.getAttribute("y"));
   		Point p = PointFactory.createPoint(x, y);
   		if (p == null)
   			throw new ExceptionXML("Error when reading file: Point coordinates must belong to the plan");
      	int rectangleWidth = Integer.parseInt(elt.getAttribute("width"));
   		if (rectangleWidth <= 0)
   			throw new ExceptionXML("Error when reading file: Rectangle width must be positive");
      	int rectangleHeight = Integer.parseInt(elt.getAttribute("height"));
   		if (rectangleHeight <= 0)
   			throw new ExceptionXML("Error when reading file: Rectangle height must be positive");
   		return new Rectangle(p, rectangleWidth, rectangleHeight);
    }
 
}
