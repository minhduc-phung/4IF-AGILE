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

/**
 *
 * @author bbbbb
 */
public interface State {
    public default void loadMapFromXML(Controller controller, Window window) throws ExceptionXML, ParserConfigurationException, IOException, SAXException {

    };
    
    public default void calculateTour(Controller controller, Courier c, Long idWarehouse) throws ParseException {
    };
    
    public default void saveDeliveryPointToFile(Controller controller) throws ParserConfigurationException, SAXException, ExceptionXML,
                                        IOException, TransformerConfigurationException, TransformerException, XPathExpressionException {    
    };
    
    public default void restoreDeliveryPointFromXML(Controller controller) 
                                                    throws ExceptionXML, ParserConfigurationException, IOException, 
                                                    SAXException, XPathExpressionException {
    };
    
    public default void selectCourier(Controller controller, Long idCourier) {
    };
    
    public default void enterDeliveryPoint(Controller controller, Map map, Long idIntersection, Long idCourier, Integer timeWindow) {
    };
    
    public default void removeDeliveryPoint(Controller controller, Map map, DeliveryPoint dp, Long idCourier) {
    };

    public default void generateDeliveryPlanForCourier(Controller controller, Courier c) throws ParserConfigurationException, SAXException, ExceptionXML,
                                                                                                IOException, TransformerException{
        
    };

    public default void mouseMovedOnMap(Controller controller, double mousePosX, double mousePosY){};

    public default void mouseClickedOnMap(Controller controller){};

    public default void mouseExitedMap(Controller controller){};

    public default void mouseClickedOnTable(Controller controller, int indexDP) { };
    
    public default void undo(ListOfCommands loc) { };
    
    public default void redo(ListOfCommands loc) { };
    
    public default void modifyTour(Controller controller) {};
    
    public default void modifyTourEnterDP(Controller controller, Courier c, Intersection intersection,
                    Integer timeWindow, ListOfCommands loc) throws ParseException {};
    
    public default void modifyTourRemoveDP(Controller controller, Courier c, DeliveryPoint dp, ListOfCommands loc) {};
    
}
