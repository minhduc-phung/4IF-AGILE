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
import javax.xml.transform.TransformerConfigurationException;
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
 * This class enables us to move between states (it transfers a state to another).
 * its methods are executed depending on the current state of the application.
 */

public class Controller {

    protected User user = new User();
    protected Map map = new Map();
    /**
     * This attribute represents the current state of the application (CourierChosenState, DPEnteredState,... )
     */
    private State currentState;
    private Window window;
    private ListOfCommands listOfCommands;

    protected final InitialState initialState = new InitialState();
    protected final MapLoadedState mapLoadedState = new MapLoadedState();
    protected final CourierChosenState courierChosenState = new CourierChosenState();
    protected final DPEnteredState dpEnteredState = new DPEnteredState();
    protected final DPRestoredState dpRestoredState = new DPRestoredState();
    protected final TourCalculatedState tourCalculatedState = new TourCalculatedState();
    protected final TourModifiedState tourModifiedState = new TourModifiedState();
    protected final PlanGeneratedState planGeneratedState = new PlanGeneratedState();
    protected final DPSavedState dpSavedState = new DPSavedState();

    /**
     * this is the constructor of the controller class it allows us to instantiate Controller objects
     */
    public Controller() {
        this.currentState = initialState;
        this.window = new Window(user, this);
        this.listOfCommands = new ListOfCommands();
    }
    /**
     * this is the setter for the currentState attribute
     * @param currentState
     */
    protected void setCurrentState(State currentState) {
        this.currentState = currentState;
    }
    /**
     * this method allows the user to load a Map from an XML file, it depends on the state of the application in a given moment so the loading of the map could be compatible with it.
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws ExceptionXML
     */
    public void loadMapFromXML() throws ParserConfigurationException, IOException, SAXException, ExceptionXML {
        System.out.println(this.currentState.getClass());
        this.currentState.loadMapFromXML(this, window);
    }
    /**
     * this method allows us to enter a delivery point depending on the current state of the application.
     * @param map
     * @param idIntersection
     * @param idCourier
     * @param timeWindow
     */
    public void enterDeliveryPoint(Map map, Long idIntersection, Long idCourier, Integer timeWindow) {
        System.out.println(this.currentState.getClass());
        this.currentState.enterDeliveryPoint(this, map, idIntersection, idCourier, timeWindow);
    }
    /**
     * this method calculates the tour.
     * @param c
     * @param idWarehouse
     * @exception ParseException
     */
    public void calculateTour(Courier c, Long idWarehouse) throws ParseException {
        System.out.println(this.currentState.getClass());
        this.currentState.calculateTour(this, c, idWarehouse);
    }
    /**
     * this method saves the delivery points in a file.
     * @exception XPathExpressionException
     * @exception ParserConfigurationException
     * @exception IOException
     * @exception TransformerException
     * @exception SAXException
     * @exception ExceptionXML
     */
    public void saveDeliveryPointToFile() throws XPathExpressionException, ParserConfigurationException, IOException, TransformerException, SAXException, ExceptionXML {
        System.out.println(this.currentState.getClass());
        this.currentState.saveDeliveryPointToFile(this);
    }
    /**
     * this method restores the delivery points from a given XML file.
     * @exception IOException
     * @exception ParserConfigurationException
     */
    public void restoreDeliveryPointFromXML() throws ParserConfigurationException, IOException,
            SAXException, XPathExpressionException, ExceptionXML {
        System.out.println(this.currentState.getClass());
        this.currentState.restoreDeliveryPointFromXML(this);
    }
    /**
     * this method removes a selected delivery point.
     * @param map
     * @param dp
     * @param idCourier
     */
    public void removeDeliveryPoint(Map map, DeliveryPoint dp, Long idCourier) {
        System.out.println(this.currentState.getClass());
        this.currentState.removeDeliveryPoint(this, map, dp, idCourier);
    }
    /**
     * this method generates the delivery plan for a given courier.
     * @param c the chosen courier
     */
    public void generatePlan(Courier c) throws ParserConfigurationException, SAXException, TransformerException, ExceptionXML, IOException {
        System.out.println(this.currentState.getClass());
        this.currentState.generateDeliveryPlanForCourier(this, c);
    }
    /**
     * this method selects a courier.
     * @param idCourier
     */
    public void selectCourier(Long idCourier) {
        System.out.println(this.currentState.getClass());
        this.currentState.selectCourier(this, idCourier);
    }

    /**
     * This method enables the user to modify the tour.
      */
    public void modifyTour() {
        System.out.println(this.currentState.getClass());
        this.currentState.modifyTour(this);
    }

    /**
     * This method enables the user to modify the tour by entering a new delivery point.
     * @param c
     * @param intersection
     * @param timeWindow
     * @throws ParseException
     */
    public void modifyTourEnterDP(Courier c, Intersection intersection, Integer timeWindow) throws ParseException {
        this.currentState.modifyTourEnterDP(this, c, intersection, timeWindow, listOfCommands);
    }

    /**
     * This method enables the user to modify the tour by removing a delivery point.
     * @param c
     * @param dp
     */
    public void modifyTourRemoveDP(Courier c, DeliveryPoint dp) {
        this.currentState.modifyTourRemoveDP(this, c, dp, listOfCommands);
    }

    /**
     * this method enables a point on the map to be selected by clicking on it, which changes its color on the map.
     */
    public void mouseClickedOnMap() {
        currentState.mouseClickedOnMap(this);
    }
    /**
     * this method shows us the nearest points when we move the mouse on the map (by changing their colors) which help us decide what point we're going to select.
     * @param mousePosX
     * @param mousePosY
     */
    public void mouseMovedOnMap(double mousePosX, double mousePosY) {
        currentState.mouseMovedOnMap(this, mousePosX, mousePosY);
    }
    /**
     * this method allows to exit the map.
     */
    public void mouseExitedMap() {
        currentState.mouseExitedMap(this);
    }
    /**
     * Getter method for the attribute map
     */
    public Map getMap() {
        return map;
    }
    /**
     * Getter method for the attribute user
     */
    public User getUser() {
        return user;
    }
    /**
     * Getter method for the attribute window
     */
    public Window getWindow() {
        return window;
    }
    /**
     * this method enables a delivery point on the table to be selected by clicking on it, which changes its color on the map.
     * @param indexDP
     */
    public void mouseClickedOnTable(int indexDP) {
        currentState.mouseClickedOnTable(this, indexDP);
    }

    /**
     * Method called by window after a click on the button "Undo"
     */
    public void undo() {
        currentState.undo(listOfCommands);
    }

    public void redo() {
        currentState.redo(listOfCommands);
    }
}
