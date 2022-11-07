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
    private State state;
    public DijsktraTest() {
    }
    
    @Before
    public void setUp() throws ParserConfigurationException, IOException, SAXException {
        map = this.controller.loadMapFromXML("maps/mediumMap.xml");
        state = new State(){ };
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void TestContain1() {
        assert(map.getIntersection(Long.parseLong("1"))==null || map.getIntersection(Long.parseLong("2"))==null);        
    }
    
    @Test
    public void TestContain2() {
        assert(map.getIntersection(Long.parseLong("2129259178")) != null && 
                    map.getIntersection(Long.parseLong("25610684")) != null);          
    }
    
    @Test
    public void TestDistance1() {
        assert(state.dijkstra(map, Long.parseLong("26149167"), Long.parseLong("143386")) == Long.MAX_VALUE);
    }
    
    @Test
    public void TestDistance2() {
        Double dist = state.dijkstra(map, Long.parseLong("26149167"), Long.parseLong("891129809"));
        assert(dist != Long.MAX_VALUE);
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
