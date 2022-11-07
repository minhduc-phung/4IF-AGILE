/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import model.Courier;
import model.DeliveryPoint;
import model.Map;
import model.User;
import org.xml.sax.SAXException;
import view.Window;

/**
 *
 * @author bbbbb
 */
public class Controller {
    private User user = new User();
    private State currentState;
    private Window window;
    private Map map;
    
    protected final InitialState initialState = new InitialState();
    protected final MapLoadedState mapLoadedState = new MapLoadedState();
    protected final CourierChosenState courierChosenState = new CourierChosenState();
    protected final DPEnteredState dpEnteredState = new DPEnteredState();
    protected final DPRestoredState dpRestoredState = new DPRestoredState();
    protected final DPRemovedState dpRemovedState = new DPRemovedState();
    protected final TourCalculatedState tourCalculatedState = new TourCalculatedState();
    protected final PlanGeneratedState planGeneratedState = new PlanGeneratedState();
    protected final DPSavedState dpSavedState = new DPSavedState();   

    public Controller() {
        this.currentState = initialState;
    }
    
    public User getUser() {
        return user;
    }

    protected void setCurrentState(State currentState) {
        this.currentState = currentState;
    }
    
    public Map loadMapFromXML(String XMLPath) throws ParserConfigurationException, IOException, SAXException {
        return this.currentState.loadMapFromXML(this, XMLPath);
    }

    public void enterDeliveryPoint(Map map, Long idIntersection, Date planDate, Long idCourier, Date timeWindow) {
        this.currentState.enterDeliveryPoint(this, map, idIntersection, planDate, idCourier, timeWindow);
    }

    public List<DeliveryPoint> restoreDeliveryPointFromXML(String XMLPathMap, String XMLPathDeliveryPoint, Date planDate)
                                                    throws ParserConfigurationException, IOException,
                                                    SAXException, XPathExpressionException {
        return this.currentState.restoreDeliveryPointFromXML(this, XMLPathMap, XMLPathDeliveryPoint, planDate);
    }

    public void calculateTour(Courier c, Long idWarehouse) throws IOException {
        this.currentState.calculateTour(this, c, idWarehouse);
    }

    public void saveDeliveryPointToFile(List<DeliveryPoint> listDP) throws XPathExpressionException, ParserConfigurationException, IOException, TransformerException, SAXException {
        this.currentState.saveDeliveryPointToFile(this, listDP);
    }

    public void removeDeliveryPoint(Map map, DeliveryPoint dp, Long idCourier) {
        this.currentState.removeDeliveryPoint(this, map, dp, idCourier);
    }



    public void generatePlan(Courier c) {
        this.currentState.generatedDeliveryPlanForCourier(this, c);
    }



    public void selectCourier(Long idCourier) {
        this.currentState.selectCourier(this, idCourier);
    }





}
