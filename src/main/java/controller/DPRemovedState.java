package controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import javax.xml.parsers.ParserConfigurationException;

import javafx.scene.paint.Color;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import tsp.CompleteGraph;
import model.Courier;
import model.DeliveryPoint;
import tsp.Graph;
import model.Intersection;
import model.Map;
import tsp.TSP;
import tsp.TSP1;
import model.User;
import org.xml.sax.SAXException;
import view.Window;
import xml.ExceptionXML;
import xml.XMLdpsDeserializer;
import xml.XMLdpsSerializer;
import xml.XMLmapDeserializer;

/**
 *
 * @author bbbbb
 */
public class DPRemovedState implements State {
        
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
        controller.getWindow().getGraphicalView().clearSelection();
        controller.getWindow().getGraphicalView().paintIntersection(dp, Color.BLUE, map);
        controller.getWindow().setMessage("Delivery point added.");
        controller.setCurrentState(controller.dpEnteredState);
        controller.getWindow().allowNode("VALIDATE_DP", false);
        controller.getWindow().allowNode("SAVE_DP", true);
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
    
    @Override
    public void saveDeliveryPointToFile(Controller controller) throws ParserConfigurationException, SAXException, ExceptionXML,
                                        IOException, TransformerConfigurationException, TransformerException, XPathExpressionException {
        Map map = controller.map;
        User user = controller.user;
        XMLdpsSerializer.getInstance().save(map, user);
        controller.getWindow().setMessage("Delivery points saved.");
        controller.setCurrentState(controller.dpSavedState);
    }

    public void selectCourier(Controller controller, Long idCourier) {
        controller.getWindow().getInteractivePane().setSelectedCourierId(idCourier);
        controller.getWindow().setMessage("Courier selected.");
        controller.getWindow().getGraphicalView().updateMap(controller.getMap(), controller.user.getCourierById(idCourier));
    }
    
    @Override
    public void restoreDeliveryPointFromXML(Controller controller) throws ExceptionXML, ParserConfigurationException, IOException, 
                                                    SAXException, XPathExpressionException {
        //precondition : Map is loaded and XMLfile of deliveryPoints exists
        Map map = controller.map;
        User user = new User();
        controller.user = XMLdpsDeserializer.loadDPList(map, user);
        controller.getWindow().setMessage("Delivery points restored successfully.");
        controller.setCurrentState(controller.dpRestoredState);
        controller.getWindow().allowNode("SAVE_DP", true);
    }

    @Override
    public void mouseMovedOnMap(Controller controller, double mousePosX, double mousePosY) {
        Double scale = controller.getWindow().getGraphicalView().getScale();
        Double minLongitude = controller.getWindow().getGraphicalView().getMinLongitude();
        Double minLatitude = controller.getWindow().getGraphicalView().getMinLatitude();
        Integer viewHeight = controller.getWindow().getGraphicalView().getViewHeight();
        // Map mouse position to latitude and longitude
        Double mouseLongtitude = mousePosX / scale + minLongitude;
        Double mouseLatitude = (viewHeight - mousePosY) / scale + minLatitude;
        Double minDistance = Double.MAX_VALUE;
        Intersection nearestIntersection = null;
        for (Intersection i : controller.getMap().getListIntersection().values()) {
            if (Objects.equals(i, controller.getMap().getWarehouse())) {
                continue;
            }
            Double distance = Math.sqrt(Math.pow(mouseLongtitude - i.getLongitude(), 2) + Math.pow(mouseLatitude - i.getLatitude(), 2));
            if (distance < minDistance) {
                minDistance = distance;
                nearestIntersection = i;
            }
        }
        Intersection oldHoveredIntersection = controller.getWindow().getGraphicalView().getHoveredIntersection();
        List<Long> dpIds = controller.user.getCourierById(controller.getWindow().getInteractivePane().getSelectedCourierId()).getDeliveryPointIds();
        dpIds.remove(0); // remove warehouse ID
        if (oldHoveredIntersection != null) {
            if (oldHoveredIntersection.equals(controller.getWindow().getGraphicalView().getSelectedIntersection())) {
                controller.getWindow().getGraphicalView().paintIntersection(oldHoveredIntersection, Color.BROWN, controller.getMap());
            } else if (dpIds.contains(oldHoveredIntersection.getId())) {
                controller.getWindow().getGraphicalView().paintIntersection(oldHoveredIntersection, Color.BLUE, controller.getMap());
            } else {
                controller.getWindow().getGraphicalView().paintIntersection(oldHoveredIntersection, Color.WHITE, controller.getMap());
            }
        }
        // Re-paint the selected intersection in case it gets overlapped by an old hovered intersection
        if (controller.getWindow().getGraphicalView().getSelectedIntersection() != null) {
            controller.getWindow().getGraphicalView().paintIntersection(controller.getWindow().getGraphicalView().getSelectedIntersection(), Color.BROWN, controller.getMap());
        }

        controller.getWindow().getGraphicalView().setHoveredIntersection(nearestIntersection);

        controller.getWindow().getGraphicalView().paintIntersection(nearestIntersection, Color.ORANGE, controller.getMap());
    }

    @Override
    public void mouseClickedOnMap(Controller controller) {
        if (controller.getWindow().getGraphicalView().getHoveredIntersection() != null) {
            if (controller.getWindow().getGraphicalView().getSelectedIntersection() != null) {
                controller.getWindow().getGraphicalView().paintIntersection(controller.getWindow().getGraphicalView().getSelectedIntersection(), Color.WHITE, controller.getMap());
            }
            controller.getWindow().getGraphicalView().setSelectedIntersection(controller.getWindow().getGraphicalView().getHoveredIntersection());
            controller.getWindow().getGraphicalView().setHoveredIntersection(null);
            controller.getWindow().getGraphicalView().paintIntersection(controller.getWindow().getGraphicalView().getSelectedIntersection(), Color.BROWN, controller.getMap());
            controller.getWindow().allowNode("VALIDATE_DP", true);
        }
    }

    @Override
    public void mouseExitedMap(Controller controller){
        Intersection hoveredIntersection = controller.getWindow().getGraphicalView().getHoveredIntersection();
        if (hoveredIntersection != null) {
            if (hoveredIntersection.equals(controller.getWindow().getGraphicalView().getSelectedIntersection())) {
                controller.getWindow().getGraphicalView().paintIntersection(hoveredIntersection, Color.BROWN, controller.getMap());
            } else {
                controller.getWindow().getGraphicalView().paintIntersection(hoveredIntersection, Color.WHITE, controller.getMap());
            }
            controller.getWindow().getGraphicalView().setHoveredIntersection(null);
        }
    }
}
