/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import controller.Controller;
import de.saxsys.mvvmfx.testingutils.jfxrunner.JfxRunner;
import javafx.embed.swing.JFXPanel;
import model.Courier;
import model.DeliveryPoint;
import model.Map;
import observer.Observable;
import observer.Observer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xml.sax.SAXException;
import xml.ExceptionXML;

//import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.embed.swing.JFXPanel;

@RunWith(JfxRunner.class)
public class CalculateTourTest {

    private Controller controller = new Controller();
    private Observer observer;
    private Boolean updateCalled;
    private Courier courier;
    private Map map;

    private JFXPanel jFXPanel;

    public CalculateTourTest() {
    }

    @Before
    public void setUp() throws ParserConfigurationException, IOException, SAXException, ExceptionXML {
        jFXPanel = new JFXPanel();
        updateCalled = false;
        observer = new Observer() {
            public void update(Observable o, Object arg) {
                updateCalled = true;
            }
        };
        controller.loadMapFromXML();
        map = controller.getMap();
        controller.selectCourier(1L);
        courier = controller.getUser().getCourierById(1L);
    }

    @Test
    public void test1() throws ParseException {
        Long[] listIdInter = {1850080438L, 2959901670L, 270298921L,
            21703589L, 26317207L, 1440845047L};
        for (int i = 0 ; i < listIdInter.length ; i++)
            controller.enterDeliveryPoint(map, listIdInter[i], courier.getId(), 8);
        controller.calculateTour(courier, map.getWarehouse().getId());
        DeliveryPoint lastPoint = courier.getDeliveryPointById(26317207L);
        SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy");
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        Date deadline = sdf.parse(sd.format(now) + " 09:10:00");
        assert(lastPoint.getEstimatedDeliveryTime().before(deadline));
    }
    
    @Test
    public void test2() throws ParseException {
        Long[] listIdInter = {1850080438L, 2959901670L, 270298921L,
                                21703589L, 26317207L, 1440845047L, 
                                459797866L, 1957527553L, 1957527548L,
                                1957527541L, 21703591L, 21703594L, 
                                1682387628L, 382011406L, 382011401L };
        for (int i = 0 ; i < listIdInter.length ; i++)
            controller.enterDeliveryPoint(map, listIdInter[i], courier.getId(), 8);
        controller.calculateTour(courier, map.getWarehouse().getId());
        DeliveryPoint lastPoint = courier.getDeliveryPointById(1682387628L);
        SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy");
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        Date deadline = sdf.parse(sd.format(now) + " 09:10:00");
        assert(lastPoint.getEstimatedDeliveryTime().before(deadline));
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
