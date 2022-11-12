/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import controller.Controller;
import controller.Service;
import java.io.FileWriter;
import model.Map;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import javafx.application.Application;
import javafx.stage.Stage;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import tsp.CompleteGraph;
import model.Courier;
import model.DeliveryPoint;
import tsp.Graph;
import model.Segment;
import tsp.TSP;
import tsp.TSP1;
import model.User;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.xml.sax.SAXException;
import xml.ExceptionXML;

/**
 *
 * @author nmngo
 */
public class Main extends Application {
    public static void main(String[] args) throws ParserConfigurationException, IOException, 
                                SAXException, ParseException, TransformerException, 
                                TransformerConfigurationException, XPathExpressionException, ExceptionXML {
        testLoadMap();
        //testSaveDeliveryPoints();
        //testRestoreDeliveryPoints();
        //testDijkstra();
        //testEnterDeliveryPoint();
        //testRemoveDeliveryPoint();
        //testCalculateTour();
    }
    
    public static void testLoadMap() throws ParserConfigurationException, IOException, SAXException, ExceptionXML {
        Controller controller = new Controller();
        controller.loadMapFromXML();
    }
    
    public static void resultReport(Result result) {
        System.out.println("Finished. Result: Failures: " +
            result.getFailureCount() + ". Ignored: " +
            result.getIgnoreCount() + ". Tests run: " +
            result.getRunCount() + ". Time: " +
            result.getRunTime() + "ms.");
    }
    
    public static void testSaveDeliveryPoints() throws ParserConfigurationException, SAXException, 
                            IOException, TransformerException, ParseException, TransformerConfigurationException, 
                            XPathExpressionException, ExceptionXML {
        Controller controller = new Controller();
        User user = controller.getUser();
        Courier c = user.getListCourier().get(Long.parseLong("4"));
    
        DeliveryPoint dp = new DeliveryPoint(Long.parseLong("1850080438"),
                                            Double.parseDouble("45.754265"), Double.parseDouble("4.886816"));
        // persist deliveryPoint and courier
        dp.chooseCourier(c);
        c.addDeliveryPoint(dp);
        // call service
        //service.saveDeliveryPointToFile(c.getCurrentDeliveryPoints());
        

        controller.setCurrentState(controller.dpEnteredState);
        
        controller.saveDeliveryPointToFile();
    }
    
    /*
    public static void testRestoreDeliveryPoints() throws ParserConfigurationException, 
                        IOException, XPathExpressionException, SAXException, ParseException {
        Service service = new Service();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd hh:mm:ss z yyyy");
        Date planDate = sdf.parse("Sun Oct 23 00:00:00 CEST 2022");
        List<DeliveryPoint> listDP = service.restoreDeliveryPointFromXML("maps/mediumMap.xml", 
                                "saved_files/deliveryPoints.xml", planDate);
        for (DeliveryPoint dp : listDP) {
            System.out.println(dp);
        }
    }
    */
    
    public static void testDijkstra() throws ParserConfigurationException, IOException, SAXException {
        JUnitCore junit = new JUnitCore();
        junit.addListener(new TextListener(System.out));
        Result result = junit.run(
                DijsktraTest.class);
        resultReport(result);
    }
    /*
    public static void testEnterDeliveryPoint() throws ParserConfigurationException, IOException, 
                                            SAXException, ParseException {
        Service service = new Service();
        Map map = service.loadMapFromXML("maps/mediumMap.xml");
        Long[] listIdInter = {Long.parseLong("2129259178"), Long.parseLong("2129259180"), Long.parseLong("239601996"),
                                Long.parseLong("21703589"), Long.parseLong("60901982")};
        Courier c = service.getUser().getCourierById(Long.parseLong("1"));

        for (Long idInter : listIdInter) {
            service.enterDeliveryPoint(map, idInter, c.getId(), Integer.parseInt("9"));
        }
       
        // shortest path of courier 1
        for (Long key : c.getShortestPathBetweenDPs().keySet()) {
            //writer.write("key:"+key+"\n");
            for (Long key2 : c.getShortestPathBetweenDPs().get(key).keySet()) {
                //writer.write(key2+":"+c.getShortestPathBetweenDPs().get(key).get(key2) + "  ");
            }
            //writer.write("\n");
        }
        System.out.println(c.getListSegmentBetweenInters(Long.parseLong("25303831"), Long.parseLong("2129259178")));
    }*/
    /*
    public static void testRemoveDeliveryPoint() throws ParserConfigurationException, IOException, SAXException, ParseException {
        Service service = new Service();
        Courier c = service.getUser().getCourierById(1L);
        Map map = service.loadMapFromXML("maps/mediumMap.xml");
        // add 20 points to listDP
        Long[] listIdInter = {Long.parseLong("2129259178"), Long.parseLong("2129259180"), Long.parseLong("239601996"),
                                Long.parseLong("21703589"), Long.parseLong("60901982")};

        for (Long idInter : listIdInter) {
            service.enterDeliveryPoint(map, idInter, c.getId(), Integer.parseInt("9"));
        } 
        
        // Delete a point
        DeliveryPoint aDP = new DeliveryPoint(Long.parseLong("60901982"), 0.0, 0.0);
        service.removeDeliveryPoint(map, aDP, c);
        for (Long key : c.getShortestPathBetweenDPs().keySet()) {
            System.out.println(key.toString() + c.getShortestPathBetweenDPs().get(key));
        }
    }
    
    public static void testCalculateTour() throws ParserConfigurationException, IOException, SAXException {
        Service service = new Service();
        Courier c = service.getUser().getCourierById(1L);
        Map map = service.loadMapFromXML("maps/mediumMap.xml");
        Long[] listIdInter = {Long.parseLong("2129259178"), Long.parseLong("2129259180"), Long.parseLong("239601996"),
                                Long.parseLong("21703589"), Long.parseLong("60901982")};
        // enter delivery points
        for (Long idInter : listIdInter) {
            service.enterDeliveryPoint(map, idInter, c.getId(), Integer.parseInt("9"));
        }  
        
        // shortest path of courier 1
        HashMap<Long, HashMap<Long, Double>> completeMap = c.getShortestPathBetweenDPs();
        int nbVertices = completeMap.size();
        Long idWarehouse = map.getWarehouse().getId();
        System.out.println("Graphs with "+nbVertices+" vertices:");
        Graph g = new CompleteGraph(c, idWarehouse);
        
        TSP tsp = new TSP1();
        long startTime = System.currentTimeMillis();
	tsp.searchSolution(200000, g);
	System.out.print("Solution of cost "+tsp.getSolutionCost()+" found in "
				+(System.currentTimeMillis() - startTime)+"ms : ");
        System.out.println("ID of points: "+ c.getPositionIntersection() );
        List<Integer> tspSolutions = new ArrayList<>();
	for (int i=0; i<nbVertices; i++) {
            System.out.print( tsp.getSolution(i)+" " );
            tspSolutions.add( tsp.getSolution(i) );
            
        }
	System.out.println("0");
        
        // Show timestamp
        double sum = 0.0;
        int i;
        System.out.println(0.0);
        DeliveryPoint dp = c.getCurrentDeliveryPoints().get(0);
        dp.assignTimestamp(0.0);
        for (i = 0 ; i < tspSolutions.size()-1 ; i++) {
            double distance = g.getCost(tspSolutions.get(i), tspSolutions.get(i+1));
            sum += distance;
            dp = c.getCurrentDeliveryPoints().get(tspSolutions.get(i+1));
            dp.assignTimestamp(sum);
            System.out.println(sum);
        }
        double distance = g.getCost(tspSolutions.get(i), tspSolutions.get(0));
        sum += distance;
        System.out.println(sum);
        
        // set currentTour
        for (i=0 ; i < c.getCurrentDeliveryPoints().size()-1 ; i++) {
            Long idCurrentInter = c.getCurrentDeliveryPoints().get(tspSolutions.get(i)).getId();
            Long idNextInter = c.getCurrentDeliveryPoints().get(tspSolutions.get(i+1)).getId();
            List<Segment> listSeg = c.getListSegmentBetweenInters(idCurrentInter, idNextInter);
            c.addCurrentTour(idCurrentInter, listSeg);
        }
        Long idCurrentInter = c.getCurrentDeliveryPoints().get(tspSolutions.get(i)).getId();
        Long idNextInter = c.getCurrentDeliveryPoints().get(tspSolutions.get(0)).getId();
        List<Segment> listSeg = c.getListSegmentBetweenInters(idCurrentInter, idNextInter);
        c.addCurrentTour(idCurrentInter, listSeg);
        
        // generate plan
        FileWriter writer = new FileWriter("text1.txt");
        for (i = 0 ; i < tspSolutions.size() ; i++) {
            DeliveryPoint deliP = c.getCurrentDeliveryPoints().get(tspSolutions.get(i));
            writer.write("Point:" + deliP.getId() + " time:" + deliP.getTimestamp() + "\n");            
            writer.write(c.getListSegmentCurrentTour(deliP.getId()).toString() + "\n");
        }
        writer.close();
        
    }*/

    @Override
    public void start(Stage stage) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
