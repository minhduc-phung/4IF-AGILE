/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import model.Courier;
import model.DeliveryPoint;
import model.Intersection;
import model.Map;
import model.Segment;
import model.Tour;
import model.User;
import org.xml.sax.SAXException;
import view.Window;
import xml.ExceptionXML;

/**
 *
 * @author bbbbb
 */
public class Controller {

    protected User user = new User();
    protected Map map = new Map();
    private State currentState;
    private Window window;
    private ListOfCommands listOfCommands;

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
        this.window = new Window(user, this);
        this.listOfCommands = new ListOfCommands();
    }

    protected void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    public void loadMapFromXML() throws ParserConfigurationException, IOException, SAXException, ExceptionXML {
        System.out.println(this.currentState.getClass());
        this.currentState.loadMapFromXML(this, window);
    }

    public void enterDeliveryPoint(Map map, Long idIntersection, Long idCourier, Integer timeWindow) {
        System.out.println(this.currentState.getClass());
        this.currentState.enterDeliveryPoint(this, map, idIntersection, idCourier, timeWindow, listOfCommands);
    }

    public void calculateTour(Courier c, Long idWarehouse) throws ParseException {
        System.out.println(this.currentState.getClass());
        this.currentState.calculateTour(this, c, idWarehouse);
    }

    public void saveDeliveryPointToFile() throws XPathExpressionException, ParserConfigurationException, IOException, TransformerException, SAXException, ExceptionXML {
        System.out.println(this.currentState.getClass());
        this.currentState.saveDeliveryPointToFile(this);
    }

    public void restoreDeliveryPointFromXML() throws ParserConfigurationException, IOException,
            SAXException, XPathExpressionException, ExceptionXML {
        System.out.println(this.currentState.getClass());
        this.currentState.restoreDeliveryPointFromXML(this);
    }

    public void removeDeliveryPoint(Map map, DeliveryPoint dp, Long idCourier) {
        System.out.println(this.currentState.getClass());
        this.currentState.removeDeliveryPoint(this, map, dp, idCourier);
    }

    public void generatePlan(Courier c) {
        System.out.println(this.currentState.getClass());
        this.currentState.generatedDeliveryPlanForCourier(this, c);
    }

    public void selectCourier(Long idCourier) {
        System.out.println(this.currentState.getClass());
        this.currentState.selectCourier(this, idCourier);
    }

    public void mouseClickedOnMap() {
        currentState.mouseClickedOnMap(this);
    }

    public void mouseMovedOnMap(double mousePosX, double mousePosY) {
        currentState.mouseMovedOnMap(this, mousePosX, mousePosY);
    }

    public void mouseExitedMap() {
        currentState.mouseExitedMap(this);
    }

    public Map getMap() {
        return map;
    }

    public User getUser() {
        return user;
    }

    public Window getWindow() {
        return window;
    }

    public void mouseClickedOnTable(int indexDP) {
        currentState.mouseClickedOnTable(this, indexDP);
    }
    
    public void undo() {
        currentState.undo(listOfCommands);
    }
    
    public void keystroke(int charCode) {
	currentState.keystroke(charCode);
    }
}
