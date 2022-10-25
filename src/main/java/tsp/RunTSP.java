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
        //Set<Long> listIdInter = map.getListIntersection().keySet();
        Long[] listIdInter = {Long.parseLong("1850080438"), Long.parseLong("2959901670"), Long.parseLong("270298921"),
                                Long.parseLong("21703589"), Long.parseLong("26317207"), Long.parseLong("1440845047"), 
                                Long.parseLong("459797866"), Long.parseLong("1957527553"), Long.parseLong("1957527548"),
                                Long.parseLong("1957527541"), Long.parseLong("21703591"), Long.parseLong("21703594"), 
                                Long.parseLong("1682387628"), Long.parseLong("382011406"), Long.parseLong("382011401")};
        Courier c = service.getUser().getCourierById(Long.parseLong("1"));
        
        //int i = 0;
        for (Long idInter : listIdInter) {
            //if (i < 7)  {
                service.enterDeliveryPoint(map, idInter, planDate, c.getId(), null);
                //i++;
            //} else break;
        }
        
        //DeliveryPoint deletedDP = new DeliveryPoint(null, Long.parseLong("55475018"), Double.parseDouble("45.75978"), Double.parseDouble("4.875795"));
        //service.removeDeliveryPoint(map, deletedDP, c);
        
        // shortest path of courier 1
        HashMap<Long, HashMap<Long, Double>> completeMap = c.getShortestPathBetweenDPs();
        int nbVertices = completeMap.size();
        Long idWarehouse = map.getWarehouse().getId();
        System.out.println("Graphs with "+nbVertices+" vertices:");
        Graph g = new CompleteGraph(c, idWarehouse);
        
        /*for (int j =0; j < g.getNbVertices(); j++) {
            for (int k =0; k < g.getNbVertices(); k++) {
                System.out.println(g.getCost(j, k));
            } 
        }*/
        
        TSP tsp = new TSP1();
        long startTime = System.currentTimeMillis();
	tsp.searchSolution(200000, g);
	System.out.print("Solution of cost "+tsp.getSolutionCost()+" found in "
				+(System.currentTimeMillis() - startTime)+"ms : ");
        System.out.println("ID of points: "+ c.getPositionIntersection() );
	for (int i=0; i<nbVertices; i++) System.out.print( tsp.getSolution(i)+" " );
	System.out.println("0");
        
        //System.out.println(c.getShortestPathBetweenDPs().get(Long.parseLong("25303831")).get(Long.parseLong("239601996")) + c.getShortestPathBetweenDPs().get(Long.parseLong("239601996")).get(Long.parseLong("60901982")) 
        //        +c.getShortestPathBetweenDPs().get(Long.parseLong("60901982")).get(Long.parseLong("21703589")) + c.getShortestPathBetweenDPs().get(Long.parseLong("21703589")).get(Long.parseLong("2129259180"))+
        //        c.getShortestPathBetweenDPs().get(Long.parseLong("2129259180")).get(Long.parseLong("2129259178"))+c.getShortestPathBetweenDPs().get(Long.parseLong("2129259178")).get(Long.parseLong("25303831")));
    }

}
