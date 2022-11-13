package tests;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import controller.Controller;
import dijkstra.Dijkstra;
import java.io.IOException;
import java.util.HashMap;
import javax.xml.parsers.ParserConfigurationException;
import model.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

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
    /*
    @Before
    public void setUp() throws ParserConfigurationException, IOException, SAXException {
        map = this.controller.loadMapFromXML("maps/mediumMap.xml");
    }
    */
    @After
    public void tearDown() {
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

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
