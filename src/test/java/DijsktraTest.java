/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import controller.Controller;
import dijkstra.Dijkstra;
import model.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import xml.ExceptionXML;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author bbbbb
 */
public class DijsktraTest {
    private Controller controller = new Controller();
    private Map map;
    private HashMap<Long, Long> precedentNode = new HashMap<>();
    public DijsktraTest() {
    }
    
    @Before
    public void setUp() throws ParserConfigurationException, IOException, SAXException, ExceptionXML {
        controller.loadMapFromXML();
        map = controller.getMap();
    }
    
    @Test
    public void TestDistance1() {
        double distance = Dijkstra.dijkstra(map, 26149167L, 143386L, precedentNode);
        assert(distance == Long.MAX_VALUE);
    }
    
    @Test
    public void TestDistance2() {
        Double dist = Dijkstra.dijkstra(map, 26149167L, 891129809L, precedentNode);
        assert(dist != Long.MAX_VALUE);
    }    
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
