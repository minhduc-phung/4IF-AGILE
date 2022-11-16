/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.text.ParseException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import model.Courier;
import model.DeliveryPoint;
import model.Intersection;
import model.Map;
import org.xml.sax.SAXException;
import view.Window;
import xml.ExceptionXML;

public interface State {

    /**
     * Method called by window when the user click on the button "Load a map"
     * @param controller the controller
     * @param window the window
     * @throws ExceptionXML
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public default void loadMapFromXML(Controller controller, Window window) throws ExceptionXML, ParserConfigurationException, IOException, SAXException {

    };

    /**
     * Method called by window when the user click on the button "Calculate tour"
     * @param controller the controller
     * @param c the courier chosen by the user
     * @param idWarehouse the id of the warehouse
     * @throws ParseException
     */
    public default void calculateTour(Controller controller, Courier c, Long idWarehouse) throws ParseException {
    };

    /**
     * Method called by window when the user click on the button "Save delivery points"
     * @param controller the controller
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws ExceptionXML
     * @throws IOException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     * @throws XPathExpressionException
     */
    public default void saveDeliveryPointToFile(Controller controller) throws ParserConfigurationException, SAXException, ExceptionXML,
                                        IOException, TransformerConfigurationException, TransformerException, XPathExpressionException {    
    };

    /**
     * Method called by window when the user click on the button "Restore delivery points"
     * @param controller the controller
     * @throws ExceptionXML
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XPathExpressionException
     */
    public default void restoreDeliveryPointFromXML(Controller controller) 
                                                    throws ExceptionXML, ParserConfigurationException, IOException, 
                                                    SAXException, XPathExpressionException {
    };

    /**
     * Method called by window when the chosen courier is changed in the ComboBox
     * @param controller the controller
     * @param idCourier the id of the new courier chosen
     */
    public default void selectCourier(Controller controller, Long idCourier) {
    };

    /**
     * Method called by window when the user click on the button "Validate" to add a delivery point
     * @param controller the controller
     * @param map the map
     * @param idIntersection the id of the intersection where the delivery point is located
     * @param idCourier the id of the courier who will deliver the delivery point
     * @param timeWindow the time window in which the courier will deliver at the delivery point
     */
    public default void enterDeliveryPoint(Controller controller, Map map, Long idIntersection, Long idCourier, Integer timeWindow) {
    };

    /**
     * Method called by window when the user click on the button "Remove" to remove a delivery point
     * @param controller the controller
     * @param map the map
     * @param dp the delivery point to remove
     * @param idCourier the id of the courier who would deliver at the delivery point
     */

    public default void removeDeliveryPoint(Controller controller, Map map, DeliveryPoint dp, Long idCourier) {
    };

    /**
     * Method called by window when the user click on the button "Generate delivery plan"
     * @param controller the controller
     * @param c the courier chosen by the user
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws ExceptionXML
     * @throws IOException
     * @throws TransformerException
     */
    public default void generateDeliveryPlanForCourier(Controller controller, Courier c) throws ParserConfigurationException, SAXException, ExceptionXML,
                                                                                                IOException, TransformerException{
        
    };

    /**
     * Method called by window when the user move the mouse on the map
     * @param controller the controller
     * @param mousePosX the x position of the mouse
     * @param mousePosY the y position of the mouse
     */
    public default void mouseMovedOnMap(Controller controller, double mousePosX, double mousePosY){};

    /**
     * Method called by window when the user click on the map
     * @param controller the controller
     */
    public default void mouseClickedOnMap(Controller controller){};

    /**
     * Method called by window when the user move the mouse out of the map
     * @param controller the controller
     */
    public default void mouseExitedMap(Controller controller){};

    /**
     * Method called by window when the user click on the table of delivery points
     * @param controller the controller
     * @param indexDP the index of the delivery point in the table
     */
    public default void mouseClickedOnTable(Controller controller, int indexDP) { };

    /**
     * Method called by window when the user do a Ctrl+Z to undo the last action
     * @param loc the list of commands made by the user
     */
    public default void undo(ListOfCommands loc) { };
    /**
     * Method called by window when the user do a Ctrl+Y to redo the last undid action
     * @param loc the list of commands made by the user
     */
    public default void redo(ListOfCommands loc){};

    /**
     * Method called by window when the user click on the button "Modify"
     * @param controller the controller
     */
    public default void modifyTour(Controller controller) {};

    /**
     * Method called by window when the user click on the button "Add" to add a delivery point to the tour
     * @param controller the controller
     * @param c the courier chosen by the user
     * @param intersection the intersection where the delivery point is located
     * @param timeWindow the time window in which the courier will deliver at the delivery point
     * @param loc the list of commands
     * @throws ParseException
     */
    public default void modifyTourEnterDP(Controller controller, Courier c, Intersection intersection,
                    Integer timeWindow, ListOfCommands loc) throws ParseException {};

    /**
     * Method called by window when the user click on the button "Remove" to remove a delivery point from the tour
     * @param controller the controller
     * @param c the courier chosen by the user
     * @param dp the delivery point to remove
     * @param loc the list of commands
     */
    public default void modifyTourRemoveDP(Controller controller, Courier c, DeliveryPoint dp, ListOfCommands loc) throws ParseException {};
    
}
