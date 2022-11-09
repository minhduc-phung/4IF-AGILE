/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.util.HashMap;
import javax.xml.parsers.ParserConfigurationException;
import model.CompleteGraph;
import model.Courier;
import model.DeliveryPoint;
import model.Graph;
import model.Intersection;
import model.Map;
import model.TSP;
import model.TSP1;
import model.User;
import org.xml.sax.SAXException;
import view.Window;
import xml.ExceptionXML;
import xml.XMLmapDeserializer;

/**
 *
 * @author bbbbb
 */
public class DPRestoredState implements State {
        
    @Override
    public void loadMapFromXML(Controller controller, Window window) throws ExceptionXML, ParserConfigurationException, SAXException, IOException {
        controller.map = XMLmapDeserializer.load(controller.map);
        controller.user = new User();
        Intersection warehouse = controller.getMap().getWarehouse();
        addWarehouse(warehouse, controller.user);
        controller.setCurrentState(controller.mapLoadedState);
        window.drawMap(controller.getMap());
        window.setMessage("Map loaded!");
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
}
