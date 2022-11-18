/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import controller.Controller;
import de.saxsys.mvvmfx.testingutils.jfxrunner.JfxRunner;
import javafx.embed.swing.JFXPanel;
import model.Map;
import observer.Observable;
import observer.Observer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xml.sax.SAXException;
import xml.ExceptionXML;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static org.junit.Assert.assertNull;

@RunWith(JfxRunner.class)
public class LoadMapTest {
    private final Controller controller = new Controller();
    private Observer observer;
    private Boolean updateCalled;
    private Map map;

    private JFXPanel jFXPanel;
    public LoadMapTest() {
    }
    
    @Before
    public void setUp() throws ParserConfigurationException, IOException, SAXException, ExceptionXML {
        jFXPanel = new JFXPanel();
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
