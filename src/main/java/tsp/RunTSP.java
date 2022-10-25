package tsp;

import controller.Service;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import model.Courier;
import model.Map;
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
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd hh:mm:ss z yyyy");
        Date planDate = sdf.parse("Sun Oct 23 00:00:00 CEST 2022");
        Map map = service.loadMapFromXML("maps/mediumMap.xml");
        Set<Long> listIdInter = map.getListIntersection().keySet();
        Courier c = service.getUser().getCourierById(Long.parseLong("1"));
        Integer i = 0;
        for (Long idInter : listIdInter) {
            if (i < 6) {
                service.enterDeliveryPoint(map, idInter, planDate, c.getId(), null);
                i++;
            } else break;
        }
        
        // shortest path of courier 1
        HashMap<Long, HashMap<Long, Double>> completeMap = c.getShortestPathBetweenDPs();
        int nbVertices = completeMap.size();
        Long idWarehouse = map.getWarehouse().getId();
        System.out.println("Graphs with "+nbVertices+" vertices:");
        Graph g = new CompleteGraph(c, idWarehouse);
        TSP tsp = new TSP1();
        long startTime = System.currentTimeMillis();
	tsp.searchSolution(20000, g);
	System.out.print("Solution of cost "+tsp.getSolutionCost()+" found in "
				+(System.currentTimeMillis() - startTime)+"ms : ");
        System.out.println("ID of points: "+ c.getPositionIntersection() );
	for (i=0; i<nbVertices; i++) System.out.print( tsp.getSolution(i)+" " );
	System.out.println("0");
    }

}
