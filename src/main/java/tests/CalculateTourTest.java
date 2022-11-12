package tests;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import controller.Controller;
import model.Map;
import observer.Observer;

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
 /*   
    @Before
    public void setUp() throws ParserConfigurationException, IOException, SAXException {
        updateCalled = false;
	map = controller.loadMapFromXML("maps/mediumMap.xml");
	observer = new Observer(){public void update(Observable o, Object arg){updateCalled = true;}};
    }
    
    @After
    public void tearDown() {
    }
*/
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
