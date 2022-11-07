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
public class CalculateTourTest {
    private Controller controller = new Controller();
    private Observer observer;
    private Boolean updateCalled;
    private Map map;
    
    public CalculateTourTest() {
    }
    
    @Before
    public void setUp() throws ParserConfigurationException, IOException, SAXException {
        updateCalled = false;
	map = controller.loadMapFromXML("maps/mediumMap.xml");
	observer = new Observer(){public void update(Observable o, Object arg){updateCalled = true;}};
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
