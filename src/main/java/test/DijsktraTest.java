package test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import controller.Controller;
import controller.State;
import java.io.IOException;
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
    public DijsktraTest() {
    }
    
    @Before
    public void setUp() throws ParserConfigurationException, IOException, SAXException {
        map = this.controller.loadMapFromXML("maps/mediumMap.xml");
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void TestDistance1() {
        assert(this.controller.dijkstra(map, Long.parseLong("26149167"), Long.parseLong("143386")) == Long.MAX_VALUE);
    }
    
    @Test
    public void TestDistance2() {
        Double dist = this.controller.dijkstra(map, Long.parseLong("26149167"), Long.parseLong("891129809"));
        assert(dist != Long.MAX_VALUE);
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
