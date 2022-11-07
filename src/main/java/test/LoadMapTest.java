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
import observer.Observable;
import observer.Observer;
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
    private Observer observer;
    private Boolean updateCalled;
    
    public LoadMapTest() {
    }
    
    @Before
    public void setUp() {
        updateCalled = false;
	observer = new Observer(){public void update(Observable o, Object arg){updateCalled = true;}};   
    }
    
    @Test
    public void loadTest1() throws ParserConfigurationException, IOException, SAXException {
        Map map = this.controller.loadMapFromXML("maps/mediumMap.xml");
        map.addObserver(observer);
        assert(updateCalled);
        assert(map.getListIntersection().size() > 0);
    } 
    
    @Test
    public void loadTest2() throws ParserConfigurationException, IOException, SAXException {
        Map map = this.controller.loadMapFromXML("maps/largeMap.xml");
        map.addObserver(observer);
        assert(updateCalled);
        assert(map.getListIntersection().size() > 0);
    } 
    
    @Test
    public void loadTest3() throws ParserConfigurationException, IOException, SAXException {
        Map map = this.controller.loadMapFromXML("maps/miniMap.xml");
        map.addObserver(observer);
        assert(updateCalled);
        assert(map.getListIntersection().size() > 0);
    }
    
    @Test
    public void TestContain1() throws ParserConfigurationException, IOException, SAXException {
        Map map = this.controller.loadMapFromXML("maps/miniMap.xml");
        map.addObserver(observer);
        assert(updateCalled);
        assert(map.getIntersection(Long.parseLong("1"))==null || map.getIntersection(Long.parseLong("2"))==null);        
    }
    
    @Test
    public void TestContain2() throws ParserConfigurationException, IOException, SAXException {
        Map map = this.controller.loadMapFromXML("maps/mediumMap.xml");
        map.addObserver(observer);
        assert(updateCalled);        
        assert(map.getIntersection(Long.parseLong("2129259178")) != null && 
                    map.getIntersection(Long.parseLong("25610684")) != null);          
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
