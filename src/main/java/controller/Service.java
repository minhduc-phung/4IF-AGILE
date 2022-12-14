/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import model.Intersection;
import model.Map;
import model.Segment;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import tsp.CompleteGraph;
import model.Courier;
import model.DeliveryPoint;
import tsp.Graph;
import tsp.TSP;
import tsp.TSP1;
import model.Tour;
import model.User;
import org.w3c.dom.Document;
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
    
    //done

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

        DeliveryPoint dpWarehouse = new DeliveryPoint(warehouse.getId(),
                warehouse.getLatitude(), warehouse.getLongitude());
        for (Long key : this.getUser().getListCourier().keySet()) {
            Courier c = this.getUser().getListCourier().get(key);
            dpWarehouse.chooseCourier(c);
            c.addDeliveryPoint(dpWarehouse);
            c.addPositionIntersection(idWarehouse);
            HashMap<Long, Double> nestedMap = new HashMap<Long, Double>();
            nestedMap.put(warehouse.getId(), Double.parseDouble("0.0"));
            c.getShortestPathBetweenDPs().put(warehouse.getId(), nestedMap);
            this.getUser().getListCourier().replace(key, c);

            Tour tour = new Tour();
            tour.addTourRoute(idWarehouse, null);
            c.getListSegmentBetweenDPs().put(idWarehouse, tour);
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

            origin.addTravelTimeToNextIntersection(idDesti, length * 60.0 / (15.0 * 1000.0));

            String name = nodeMap.getNamedItem("name").getNodeValue();
            Segment segment = new Segment(origin, destination, length, name);
            listSegment.add(segment);
        }

        Map map = new Map(XMLPath, listIntersection, listSegment, warehouse);
        return map;
    }


    public void addShortestPathBetweenDP(Map aMap, Courier c, DeliveryPoint aDP) {
        List<DeliveryPoint> listDP = c.getCurrentDeliveryPoints();
        HashMap<Long, Double> distanceFromADP = new HashMap<>();
        Tour tour1 = new Tour();

        for (DeliveryPoint dp : listDP) {
            HashMap<Long, Long> precedentNode1 = new HashMap<>();
            Double dist = dijkstra(aMap, dp.getId(), aDP.getId(), precedentNode1);
            List<Segment> listSeg = new ArrayList<>();
            Long key = aDP.getId();
            while (precedentNode1.get(key) != null) {
                Segment seg = aMap.getSegment(precedentNode1.get(key), key);
                listSeg.add(seg);
                key = precedentNode1.get(key);
            }
            Collections.reverse(listSeg);

            Tour tour = new Tour();
            tour.addTourRoute(aDP.getId(), listSeg);
            if (c.getShortestPathBetweenDPs().get(dp.getId()) == null) {
                HashMap<Long, Double> nestedMap = new HashMap<Long, Double>();
                nestedMap.put(aDP.getId(), dist);
                c.getShortestPathBetweenDPs().replace(dp.getId(), nestedMap);

                c.getListSegmentBetweenDPs().replace(dp.getId(), tour);
            } else {
                c.getShortestPathBetweenDPs().get(dp.getId()).put(aDP.getId(), dist);
                c.getListSegmentBetweenDPs().get(dp.getId()).getTourRoute().put(aDP.getId(), listSeg);
            }

            HashMap<Long, Long> precedentNode2 = new HashMap<>();
            Double invertedDist = this.dijkstra(aMap, aDP.getId(), dp.getId(), precedentNode2);
            distanceFromADP.put(dp.getId(), invertedDist);

            List<Segment> listSeg1 = new ArrayList<>();
            key = dp.getId();
            while (precedentNode2.get(key) != null) {
                Segment seg = aMap.getSegment(precedentNode2.get(key), key);
                listSeg1.add(seg);
                key = precedentNode2.get(key);
            }
            Collections.reverse(listSeg1);
            tour1.getTourRoute().put(dp.getId(), listSeg1);
        }
        c.getShortestPathBetweenDPs().put(aDP.getId(), distanceFromADP);
        c.getListSegmentBetweenDPs().put(aDP.getId(), tour1);
    }

    public void removeShortestPathBetweenDP(Courier c, DeliveryPoint aDP) {
        c.getShortestPathBetweenDPs().remove(aDP.getId());
        c.getListSegmentBetweenDPs().remove(aDP.getId());
        for (DeliveryPoint dp : c.getCurrentDeliveryPoints()) {
            c.getShortestPathBetweenDPs().get(dp.getId()).remove(aDP.getId());
            c.getListSegmentBetweenDPs().get(dp.getId()).getTourRoute().remove(aDP.getId());
        }
    }

    public Double dijkstra(Map aMap, Long idOrigin, Long idDest, HashMap<Long, Long> precedentNode) {
        List<Long> idWhiteNodes = new ArrayList<>();
        List<Long> idGreyNodes = new ArrayList<>();
        List<Long> idBlackNodes = new ArrayList<>();

        HashMap<Long, Double> distanceFromOrigin = new HashMap<>();

        for (Long id : aMap.getListIntersection().keySet()) {
            distanceFromOrigin.put(id, Double.MAX_VALUE);
            precedentNode.put(id, null);
            idWhiteNodes.add(id);
        }

        distanceFromOrigin.replace(idOrigin, 0.0);
        idGreyNodes.add(idOrigin);
        idWhiteNodes.remove(idOrigin);

        Long idGreyDistanceMin = Long.MAX_VALUE;
        Double distanceFromGreyMin = Double.MAX_VALUE;

        while (!idGreyNodes.isEmpty()) {
            for (Long idGrey : idGreyNodes) {
                if (distanceFromGreyMin > distanceFromOrigin.get(idGrey)) {
                    idGreyDistanceMin = idGrey;
                    distanceFromGreyMin = distanceFromOrigin.get(idGrey);
                }
            }
            Intersection greyMin = aMap.getIntersection(idGreyDistanceMin);
            for (Long idSucc : greyMin.getTimeToConnectedIntersection().keySet()) {
                if (idWhiteNodes.contains(idSucc) || idGreyNodes.contains(idSucc)) {
                    //next 5 lines: relacher()
                    Double newDistance = distanceFromOrigin.get(idGreyDistanceMin) + greyMin.getTimeToConnectedIntersection().get(idSucc);
                    if (distanceFromOrigin.get(idSucc) > newDistance) {
                        distanceFromOrigin.replace(idSucc, newDistance);
                        precedentNode.replace(idSucc, idGreyDistanceMin);
                    }
                    if (idWhiteNodes.contains(idSucc)) {
                        idWhiteNodes.remove(idSucc);
                        idGreyNodes.add(idSucc);
                    }
                }
            }
            idBlackNodes.add(idGreyDistanceMin);
            idGreyNodes.remove(idGreyDistanceMin);
            distanceFromGreyMin = Double.MAX_VALUE;
        }
        return distanceFromOrigin.get(idDest);
    }
    
    /*
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

        // root element
        Node maps = doc.getElementsByTagName("maps").item(0);
        XPath xPath = XPathFactory.newInstance().newXPath();

        //child element "map"
        String expression = "/maps/map[@src='" + this.user.getMapSource() + "']";
        XPathExpression xPathExpression = xPath.compile(expression);
        Element map = (Element) xPathExpression.evaluate(doc, XPathConstants.NODE);

        try {
            maps.appendChild(map);
        } catch (NullPointerException e) {
            map = doc.createElement("map");
            map.setAttribute("src", this.user.getMapSource());
            maps.appendChild(map);
        }

        //child element "courier"       
        for (Long idCourier : this.user.getListCourier().keySet()) {
            Courier c = this.user.getCourierById(idCourier);
            expression = "/maps/map[@src='" + this.user.getMapSource() + "']/courier[@id='" + c.getId().toString() + "']";
            Element elementCourier = (Element) xPath.compile(expression).evaluate(doc, XPathConstants.NODE);
            try {
                map.appendChild(elementCourier);
            } catch (NullPointerException e) {
                elementCourier = doc.createElement("courier");
                elementCourier.setAttribute("id", c.getId().toString());
                map.appendChild(elementCourier);
            }

            //child element deliveryPoint
            for (DeliveryPoint dp : c.getCurrentDeliveryPoints()) {
                expression = "/maps/map[@src='" + this.user.getMapSource() + "']/courier[@id='" + c.getId().toString()
                        + "']/deliveryPoint[@id='" + dp.getId().toString() + "' and @timeWindow='" + dp.getTimeWindow() + "']";
                Element elementDeliveryPoint = (Element) xPath.compile(expression).evaluate(doc, XPathConstants.NODE);
                try {
                    elementCourier.appendChild(elementDeliveryPoint);
                } catch (NullPointerException e) {
                    elementDeliveryPoint = doc.createElement("deliveryPoint");
                    elementDeliveryPoint.setAttribute("id", dp.getId().toString());
                    elementDeliveryPoint.setAttribute("timeWindow", dp.getTimeWindow().toString());
                    elementCourier.appendChild(elementDeliveryPoint);
                }
            }
        }

        // Export the XMLFile
        transformer.transform(source, result);
    }

    public void restoreDeliveryPointFromXML(String XMLPathDeliveryPoint)
            throws ParserConfigurationException, IOException,
            SAXException, XPathExpressionException, TransformerConfigurationException, TransformerException {
        //precondition : Map is loaded and XMLfile of deliveryPoints exists
        Map map = this.loadMapFromXML(this.user.getMapSource());
        File XMLFileDP = new File(XMLPathDeliveryPoint);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbf.newDocumentBuilder();
        Document doc = dBuilder.parse(XMLFileDP);
        doc.getDocumentElement().normalize();

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        FileWriter writer = new FileWriter(XMLFileDP);
        StreamResult result = new StreamResult(writer);
        DOMSource source = new DOMSource(doc);

        //get Courier list
        XPath xPath = XPathFactory.newInstance().newXPath();
        String expression = "maps/map[@src='" + this.user.getMapSource() + "']/courier";
        NodeList nodeListCourier = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);

        //for each courier in the list, get the list of delivery point and then add these points to courier.currentDeliveryPoints
        for (int i = 0; i < nodeListCourier.getLength(); i++) {
            Long idCourier = Long.valueOf(nodeListCourier.item(i).getAttributes().getNamedItem("id").getNodeValue());
            Courier c = this.user.getCourierById(idCourier);

            NodeList nodeListDP = nodeListCourier.item(i).getChildNodes();
            for (int j = 0; j < nodeListDP.getLength(); j++) {
                String idDP = nodeListDP.item(i).getAttributes().getNamedItem("id").getNodeValue();
                Intersection inter = map.getListIntersection().get(Long.valueOf(idDP));
                DeliveryPoint dp = new DeliveryPoint(inter.getId(), inter.getLatitude(), inter.getLongitude());
                dp.chooseCourier(c);
                c.addDeliveryPoint(dp);
                nodeListCourier.item(i).removeChild(nodeListCourier.item(i).getFirstChild());
            }

        }

        // Export the XMLFile
        transformer.transform(source, result);
    }
*/

    public HashMap<Long, Courier> getListAllCouriers() {
        return this.user.getListCourier();
    }

    public Courier selectCourier(Long idCourier) {
        return this.user.getCourierById(idCourier);
    }

    public void enterDeliveryPoint(Map map, Long idIntersection, Long idCourier, Integer TW) {
        Intersection i = map.getIntersection(idIntersection);
        if (idIntersection.equals(map.getWarehouse().getId())) {
            return;
        }
        DeliveryPoint dp = new DeliveryPoint(idIntersection, i.getLatitude(), i.getLongitude());
        Courier c = user.getCourierById(idCourier);
        dp.assignTimeWindow(TW);
        dp.chooseCourier(c);
        c.addDeliveryPoint(dp);
        c.addPositionIntersection(idIntersection);
        if (!c.getShortestPathBetweenDPs().isEmpty()) {
            this.addShortestPathBetweenDP(map, c, dp);
        } else {
            c.getShortestPathBetweenDPs().put(dp.getId(), new HashMap<>());
            c.getListSegmentBetweenDPs().put(dp.getId(), new Tour());
        }
    }

    public void removeDeliveryPoint(Map map, DeliveryPoint dp, Courier c) {
        if (dp.getId().equals(map.getWarehouse().getId())) {
            return;
        }
        dp.chooseCourier(null);
        c.removeDeliveryPoint(dp);
        c.getPositionIntersection().remove(dp.getId());
        this.removeShortestPathBetweenDP(c, dp);
    }

    public Double calculateTour(Courier c, Long idWarehouse) {
        Graph g = new CompleteGraph(c, idWarehouse);
        TSP tsp = new TSP1();
        tsp.searchSolution(20000, g);
        return tsp.getSolutionCost();
    }

    public void generatedDeliveryPlanForCourier(Controller controller, Map map, Courier c) {
        // set timestamp for each delivery point

        // set currentTour
    }
}
