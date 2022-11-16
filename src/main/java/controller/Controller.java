/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.text.ParseException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import model.Courier;
import model.DeliveryPoint;
import model.Intersection;
import model.Map;
import model.User;
import org.xml.sax.SAXException;
import view.Window;
import xml.ExceptionXML;

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
    protected final TourCalculatedState tourCalculatedState = new TourCalculatedState();
    protected final TourModifiedState tourModifiedState = new TourModifiedState();
    protected final PlanGeneratedState planGeneratedState = new PlanGeneratedState();
    protected final DPSavedState dpSavedState = new DPSavedState();

    /**
     * Create the controller of the application
     */
    public Controller() {
        this.currentState = initialState;
        this.window = new Window(user, this);
        this.listOfCommands = new ListOfCommands();
    }
    /**
     * Change the current state of the controller
     * @param currentState the new current state
     */
    protected void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    // Methods corresponding to user events
    /**
     * Method called by window when the user click on the button "Load a map"
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws ExceptionXML
     * @throws SAXException
     * @throws ExceptionXML
     */
    public void loadMapFromXML() throws ParserConfigurationException, IOException, SAXException, ExceptionXML {
        System.out.println(this.currentState.getClass());
        this.currentState.loadMapFromXML(this, window);
    }

    /**
     * Method called by window when the user click on the button "Validate"
     */
    public void enterDeliveryPoint(Map map, Long idIntersection, Long idCourier, Integer timeWindow) {
        System.out.println(this.currentState.getClass());
        this.currentState.enterDeliveryPoint(this, map, idIntersection, idCourier, timeWindow);
    }

    /**
     * Method called by window when the user click on the button "Calculate tour"
     * @param c the courier chosen in the comboBox "Courier"
     * @param idWarehouse the id of the warehouse
     * @throws ParseException
     */
    public void calculateTour(Courier c, Long idWarehouse) throws ParseException {
        System.out.println(this.currentState.getClass());
        this.currentState.calculateTour(this, c, idWarehouse);
    }

    /**
     * Method called by window when the user click on the button "Save delivery points"
     * @throws XPathExpressionException
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws TransformerException
     * @throws SAXException
     * @throws ExceptionXML
     */
    public void saveDeliveryPointToFile() throws XPathExpressionException, ParserConfigurationException, IOException, TransformerException, SAXException, ExceptionXML {
        System.out.println(this.currentState.getClass());
        this.currentState.saveDeliveryPointToFile(this);
    }

    /**
     * Method called by the window when the user click on the button "Restore delivery points"
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XPathExpressionException
     * @throws ExceptionXML
     */
    public void restoreDeliveryPointFromXML() throws ParserConfigurationException, IOException,
            SAXException, XPathExpressionException, ExceptionXML {
        System.out.println(this.currentState.getClass());
        this.currentState.restoreDeliveryPointFromXML(this);
    }

    /**
     * Method called by the window when the user click on the button "Remove"
     * @param map the map that is currently loaded
     * @param dp the delivery point to remove
     * @param idCourier the id of the courier chosen from the comboBox "Courier"
     */
    public void removeDeliveryPoint(Map map, DeliveryPoint dp, Long idCourier) {
        System.out.println(this.currentState.getClass());
        this.currentState.removeDeliveryPoint(this, map, dp, idCourier);
    }

    /**
     * Method called by the window when the user click on the button "Generate delivery plan"
     * @param c the courier chosen in the comboBox "Courier"
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws TransformerException
     * @throws ExceptionXML
     * @throws IOException
     */
    public void generatePlan(Courier c) throws ParserConfigurationException, SAXException, TransformerException, ExceptionXML, IOException {
        System.out.println(this.currentState.getClass());
        this.currentState.generateDeliveryPlanForCourier(this, c);
    }

    /**
     * Method called by the window when the user change the courier in the ComboBox "Courier"
     * @param idCourier the id of the courier chosen
     */
    public void selectCourier(Long idCourier) {
        System.out.println(this.currentState.getClass());
        this.currentState.selectCourier(this, idCourier);
    }

    /**
     * Method called by the window when the user click on the button "Modify"
     */
    public void modifyTour() {
        System.out.println(this.currentState.getClass());
        this.currentState.modifyTour(this);
    }

    /**
     * Method called by the window when the user click on the button "Add" after
     * having calculated the tour and clicked on "Modify"
     */
    public void modifyTourEnterDP(Courier c, Intersection intersection, Integer timeWindow) throws ParseException {
        this.currentState.modifyTourEnterDP(this, c, intersection, timeWindow, listOfCommands);
    }

    /**
     * Method called by the window when the user click on the button "Remove" after
     * having calculated the tour and clicked on "Modify"
     */
    public void modifyTourRemoveDP(Courier c, DeliveryPoint dp) throws ParseException {
        this.currentState.modifyTourRemoveDP(this, c, dp, listOfCommands);
    }

    /**
     * Method called by the window when the user click on the map (the GraphicalView object)
     */
    public void mouseClickedOnMap() {
        currentState.mouseClickedOnMap(this);
    }

    /**
     * Method called by the window when the user move the mouse on the map (the GraphicalView object)
     * @param mousePosX the x position of the mouse on the map
     * @param mousePosY the y position of the mouse on the map
     */
    public void mouseMovedOnMap(double mousePosX, double mousePosY) {
        currentState.mouseMovedOnMap(this, mousePosX, mousePosY);
    }

    /**
     * Method called by the window when the user move the mouse out of the map (the GraphicalView object)
     */
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

    /**
     * Method called by the window when the user click on the table of delivery points
     */
    public void mouseClickedOnTable(int indexDP) {
        currentState.mouseClickedOnTable(this, indexDP);
    }

    /**
     * Method called by the window when the user used the Ctrl+Z shortcut
     */
    public void undo() {
        currentState.undo(listOfCommands);
    }
    /**
     * Method called by the window when the user used the Ctrl+Y shortcut
     */
    public void redo() {
        currentState.redo(listOfCommands);
    }
}
