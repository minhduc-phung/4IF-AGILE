package test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import controller.Controller;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import model.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;

/**
 *
 * @author bbbbb
 */
public class LoadMapTest {
    private final Controller controller = new Controller();
    
    public LoadMapTest() {
    }
    
    @Before
    public void setUp() {
   
    }
    
    @Test
    public void loadTest1() throws ParserConfigurationException, IOException, SAXException {
        Map map = this.controller.loadMapFromXML("maps/mediumMap.xml");
        assert(map.getListIntersection().size() > 0);
    } 
    
    @Test
    public void loadTest2() throws ParserConfigurationException, IOException, SAXException {
        Map map = this.controller.loadMapFromXML("maps/largeMap.xml");
        assert(map.getListIntersection().size() > 0);
    } 
    
    @Test
    public void loadTest3() throws ParserConfigurationException, IOException, SAXException {
        Map map = this.controller.loadMapFromXML("maps/miniMap.xml");
        assert(map.getListIntersection().size() > 0);
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
