/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import model.Courier;
import model.DeliveryPoint;
import model.Intersection;
import model.Map;
import org.xml.sax.SAXException;

/**
 *
 * @author bbbbb
 */
public interface State {
    public default Map loadMapFromXML(Controller controller, String XMLPath) throws ParserConfigurationException, IOException, SAXException {
        return null;
    }
    
    public default void addShortestPathBetweenDP(Map aMap, Courier c, DeliveryPoint aDP) {
        List<DeliveryPoint> listDP = c.getCurrentDeliveryPoints();
        HashMap<Long, Double> distanceFromADP = new HashMap<>();
        for (DeliveryPoint dp : listDP) {
            Double dist = dijkstra(aMap, dp.getId(), aDP.getId());
            if (c.getShortestPathBetweenDPs().get(dp.getId()) == null){
                HashMap<Long, Double> nestedMap = new HashMap<Long, Double>();
                nestedMap.put(aDP.getId(), dist);
                c.getShortestPathBetweenDPs().replace(dp.getId(), nestedMap);
            } else {
                c.getShortestPathBetweenDPs().get(dp.getId()).put(aDP.getId(), dist);
            }
            Double invertedDist = this.dijkstra(aMap, aDP.getId(), dp.getId());
            distanceFromADP.put(dp.getId(), invertedDist);
        }
        c.getShortestPathBetweenDPs().put(aDP.getId(), distanceFromADP);    
    }
    
    public default void removeShortestPathBetweenDP(Courier c, DeliveryPoint aDP) {
        //System.out.println(c.removeDeliveryPoint(aDP));
        c.getShortestPathBetweenDPs().remove(aDP.getId());
        for (DeliveryPoint dp : c.getCurrentDeliveryPoints()) {
            c.getShortestPathBetweenDPs().get(dp.getId()).remove(aDP.getId());
        }
    }
    
    public default Double dijkstra (Map aMap, Long idOrigin, Long idDest) {
        List<Long> idWhiteNodes = new ArrayList<>();
        List<Long> idGreyNodes = new ArrayList<>();
        List<Long> idBlackNodes = new ArrayList<>();
        
        HashMap<Long, Long> precedentNode = new HashMap<>();
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
    
    public default Double calculateTour(Controller controller, Courier c, Long idWarehouse) throws IOException {
        return null;
    }
    
    public default void saveDeliveryPointToFile(Controller controller, List<DeliveryPoint> listDP) throws ParserConfigurationException, SAXException, 
                                        IOException, TransformerConfigurationException, TransformerException, XPathExpressionException {    
    }
    
    public default List<DeliveryPoint> restoreDeliveryPointFromXML(Controller controller, String XMLPathMap, String XMLPathDeliveryPoint, Date planDate) 
                                                    throws ParserConfigurationException, IOException, 
                                                    SAXException, XPathExpressionException {
        return null;
    }
    
    public default Courier selectCourier(Controller controller, Long idCourier) {
        return null;
    }
    
    public default void enterDeliveryPoint(Controller controller, Map map, Long idIntersection, Date planDate, Long idCourier, Date timeWindow) {
    }
    
    public default void removeDeliveryPoint(Controller controller, Map map, DeliveryPoint dp, Long idCourier){
    }

    public default void generatedDeliveryPlanForCourier(Controller controller, Courier c) {
        
    }
}
