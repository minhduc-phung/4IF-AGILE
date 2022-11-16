package tests;

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
import xml.ExceptionXML;

public class LoadMapTest {
    private final Controller controller = new Controller();
    private Observer observer;
    private Boolean updateCalled;
    private Map map;
    
    public LoadMapTest() {
    }
    
    @Before
    public void setUp() throws ParserConfigurationException, IOException, SAXException, ExceptionXML {
        updateCalled = false;
	observer = new Observer(){public void update(Observable o, Object arg){updateCalled = true;}};   
        this.controller.loadMapFromXML();
        map = controller.getMap();
        map.addObserver(observer);
    }
    
    @Test
    public void loadTest1(){
        assert(updateCalled);
        assert(map.getListIntersection().size() > 0);
    } 
    
    @Test
    public void loadTest2() {
        assert(updateCalled);
        assert(map.getListIntersection().size() > 0);
    } 
    
    @Test
    public void loadTest3() {
        assert(map != null);
    }
    
    @Test
    public void TestContain1() {
        assert(updateCalled);
        assertNull( map.getIntersection(Long.parseLong("1")) );        
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
