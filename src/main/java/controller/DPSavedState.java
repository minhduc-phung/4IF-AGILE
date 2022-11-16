/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import javafx.scene.control.ComboBox;
import javafx.scene.paint.Color;
import model.Courier;
import model.DeliveryPoint;
import model.Intersection;
import model.Map;
import model.Segment;
import model.Tour;
import model.User;
import org.xml.sax.SAXException;
import tsp.CompleteGraph;
import tsp.Graph;
import tsp.TSP;
import tsp.TSP1;
import view.Window;
import xml.ExceptionXML;
import xml.XMLdpsDeserializer;
import xml.XMLmapDeserializer;

public class DPSavedState implements State {
        
    @Override
    public void loadMapFromXML(Controller controller, Window window) throws ExceptionXML, ParserConfigurationException, SAXException, IOException {
        controller.map = XMLmapDeserializer.load(controller.map);
        controller.user = new User();
        Intersection warehouse = controller.getMap().getWarehouse();
        addWarehouse(warehouse, controller.user);
        controller.setCurrentState(controller.mapLoadedState);
        window.getGraphicalView().drawMap(controller.getMap());
        window.getInteractivePane().resetComboBoxes();
        window.getTextualView().updateData(controller.user, 1L);
        window.allowNode("COURIER_BOX", true);
        window.allowNode("TW_BOX", true);
        window.resetLateDeliveryNumber();
        window.setMessage("Please choose a courier and a time-window to start adding delivery points.");
    }

    @Override
    public void selectCourier(Controller controller, Long idCourier) {
        controller.getWindow().getInteractivePane().setSelectedCourierId(idCourier);
        controller.getWindow().getTextualView().updateData(controller.getUser(), idCourier);
        controller.getWindow().getGraphicalView().clearSelection();
        controller.getWindow().setMessage("Courier " + controller.user.getCourierById(idCourier).getName() + " selected.");
        controller.getWindow().getGraphicalView().updateMap(controller.getMap(), controller.user.getCourierById(idCourier));
        controller.getWindow().allowNode("VALIDATE_DP", false);
        controller.getWindow().allowNode("REMOVE_DP", false);
        controller.getWindow().resetLateDeliveryNumber();
    }

    /**
     * Method which adds the warehouse as the first node of the tour of all the couriers
     * @param warehouse the warehouse
     * @param user the user
     */
    private void addWarehouse(Intersection warehouse, User user) {
        DeliveryPoint dpWarehouse = new DeliveryPoint(warehouse.getId(), warehouse.getLatitude(), warehouse.getLongitude());
        for (Long key : user.getListCourier().keySet()) {
            Courier c = user.getListCourier().get(key);
            dpWarehouse.chooseCourier(c);
            c.addDeliveryPoint(dpWarehouse);

            c.addPositionIntersection(warehouse.getId());
            HashMap<Long, Double> nestedMap = new HashMap<>();
            nestedMap.put(warehouse.getId(), Double.valueOf("0.0"));
            c.getShortestPathBetweenDPs().put(warehouse.getId(), nestedMap);
            
            Tour tour = new Tour();
            tour.addTourRoute(dpWarehouse.getId(), new ArrayList<>());
            c.getListSegmentBetweenDPs().put(dpWarehouse.getId(), tour);
            user.getListCourier().replace(key, c);
        }
    }       
    
    @Override
    public void restoreDeliveryPointFromXML(Controller controller) throws ExceptionXML, ParserConfigurationException, IOException, 
                                                    SAXException, XPathExpressionException {
        //precondition : Map is loaded and XMLfile of deliveryPoints exists
        Map map = controller.map;
        User user = new User();
        controller.user = XMLdpsDeserializer.loadDPList(map, user);
        controller.getWindow().setMessage("Delivery points restored.");
        controller.setCurrentState(controller.dpRestoredState);
        controller.getWindow().allowNode("SAVE_DP", true);
        controller.getWindow().allowNode("CALCULATE_TOUR", true);
        ((ComboBox<String>) controller.getWindow().lookup("#COURIER_BOX")).setValue(controller.getUser().getListCourierName()[0]);
    }
    
    @Override
    public void calculateTour(Controller controller, Courier c, Long idWarehouse) throws ParseException {
//        System.out.println(DPEnteredState.class.toGenericString());
        int i;
        int nbVertices = c.getCurrentDeliveryPoints().size();
        Graph g = new CompleteGraph(c, idWarehouse);
        TSP tsp = new TSP1();
        tsp.searchSolution(20000, g);

        // take the earliest time window in ListCurrentDPs
        Integer earliestTW = c.getCurrentDeliveryPoints().get(0).getTimeWindow();

        Date now = new Date();
        SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date timeStamp;
        if (earliestTW < 10) {
            timeStamp = sdf.parse(sd.format(now) + " 0" + earliestTW + ":00:00");
        } else {
            timeStamp = sdf.parse(sd.format(now) + " " + earliestTW + ":00:00");
        }

        List<Integer> tspSolutions = new ArrayList<>();
        for (i = 0; i < nbVertices; i++) {
            tspSolutions.add(tsp.getSolution(i));
            if (tsp.getSolution(i) == null){
                controller.getWindow().setMessage("No possible tour found for this set of intersections.\nPlease try again with a different set of intersections.");
                return;
            }
        }

        //timeStamp of warehouse
        DeliveryPoint dp = c.getCurrentDeliveryPoints().get(0);
        long sum = timeStamp.getTime();
        dp.setEstimatedDeliveryTime(timeStamp);

        //timeStamp of other DPs
        for (i = 0; i < tspSolutions.size() - 1; i++) {
            long timeInMinute = (long) Math.ceil(g.getCost(tspSolutions.get(i), tspSolutions.get(i + 1)) * 60 * 1000);
            sum += timeInMinute - 5 * 60 * 1000;        //5 mins for delivery
            dp = c.getCurrentDeliveryPoints().get(tspSolutions.get(i + 1));
            Date estimatedDeliveryTime = new Date(sum);
            Date timeWin = new Date();
            if (dp.getTimeWindow().compareTo(10) < 0) {
                timeWin = sdf.parse(sd.format(now) + " 0" + dp.getTimeWindow() + ":00:00");
            } else {
                timeWin = sdf.parse(sd.format(now) + " " + dp.getTimeWindow() + ":00:00");
            }
            if (estimatedDeliveryTime.before(timeWin)) {
                sum = timeWin.getTime();    
                estimatedDeliveryTime.setTime(sum);
            }
            dp.setEstimatedDeliveryTime(estimatedDeliveryTime);
            c.addTimeStampForDP(dp.getId(), dp.getEstimatedDeliveryTime());
        }

        // set currentTour
        for (i = 0; i < c.getCurrentDeliveryPoints().size() - 1; i++) {
            Long idCurrentInter = c.getCurrentDeliveryPoints().get(tspSolutions.get(i)).getId();
            Long idNextInter = c.getCurrentDeliveryPoints().get(tspSolutions.get(i + 1)).getId();
            List<Segment> listSeg = c.getListSegmentBetweenInters(idCurrentInter, idNextInter);
            for (Segment seg : listSeg) {
                controller.getWindow().getGraphicalView().paintArrow(seg, Color.web("0x00B0FF"));
            }
            c.addCurrentTour(idCurrentInter, listSeg);
        }
        Long idCurrentInter = c.getCurrentDeliveryPoints().get(tspSolutions.get(i)).getId();
        Long idNextInter = c.getCurrentDeliveryPoints().get(tspSolutions.get(0)).getId();
        List<Segment> listSeg = c.getListSegmentBetweenInters(idCurrentInter, idNextInter);
        c.addCurrentTour(idCurrentInter, listSeg);

        int lateDeliveryCount = controller.getWindow().getGraphicalView().updateCalculatedMap(controller.getMap(), c);

        controller.getWindow().getTextualView().updateData(controller.user, c.getId());
        controller.getWindow().getTextualView().getTableView().sort();
        controller.getWindow().setMessage("The tour has been calculated.");
        controller.getWindow().allowNode("MODIFY_DP", true);
        controller.getWindow().allowNode("GENERATE_PLAN", true);
        controller.getWindow().allowNode("LOAD_MAP", false);
        controller.getWindow().allowNode("CALCULATE_TOUR", false);
        controller.getWindow().allowNode("RESTORE_DP", false);
        controller.getWindow().allowNode("SAVE_DP", false);
        controller.getWindow().updateOnCalculateTour(lateDeliveryCount);
        controller.setCurrentState(controller.tourCalculatedState);
    }


}
