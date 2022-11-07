package test;

import controller.Service;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import model.CompleteGraph;
import model.Courier;
import model.Courier;
import model.Graph;
import model.Map;
import model.Map;
import model.TSP;
import model.TSP1;
import org.xml.sax.SAXException;

public class RunTSP {
    public static void main(String[] args) throws ParserConfigurationException, 
            IOException, SAXException, ParseException {
                /*TSP tsp = new TSP1();
		for (int nbVertices = 8; nbVertices <= 16; nbVertices += 2){
			System.out.println("Graphs with "+nbVertices+" vertices:");
			Graph g = new CompleteGraph(nbVertices);
			long startTime = System.currentTimeMillis();
			tsp.searchSolution(20000, g);
			System.out.print("Solution of cost "+tsp.getSolutionCost()+" found in "
					+(System.currentTimeMillis() - startTime)+"ms : ");
			for (int i=0; i<nbVertices; i++)
				System.out.print(tsp.getSolution(i)+" ");
			System.out.println("0");
		} */
            testTsp();    
	}
        
    public static void testTsp() throws ParserConfigurationException, IOException, SAXException, ParseException {
        Service service = new Service();
        Map map = service.loadMapFromXML("maps/mediumMap.xml");
        //Set<Long> listIdInter = map.getListIntersection().keySet();
        Long[] listIdInter = {Long.parseLong("1850080438"), Long.parseLong("2959901670"), Long.parseLong("270298921"),
                                Long.parseLong("21703589"), Long.parseLong("26317207"), Long.parseLong("1440845047"), 
                                Long.parseLong("459797866"), Long.parseLong("1957527553"), Long.parseLong("1957527548")};
                                /*Long.parseLong("1957527541"), Long.parseLong("21703591"), Long.parseLong("21703594"), 
                                Long.parseLong("1682387628"), Long.parseLong("382011406"), Long.parseLong("382011401") */
        Courier c = service.getUser().getCourierById(Long.parseLong("1"));
        
        for (Long idInter : listIdInter) {
            service.enterDeliveryPoint(map, idInter, c.getId(), Integer.parseInt("8"));
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
	for (int i=0; i<nbVertices; i++) System.out.print( tsp.getSolution(i)+" " );
	System.out.println("0");
        
    }

}
