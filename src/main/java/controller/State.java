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
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import model.Courier;
import model.DeliveryPoint;
import model.Map;
import org.xml.sax.SAXException;
import xml.ExceptionXML;

/**
 *
 * @author bbbbb
 */
public interface State {
    public default void loadMapFromXML(Controller controller) throws ExceptionXML, ParserConfigurationException, IOException, SAXException {

    };
    
    public default Double calculateTour(Controller controller, Courier c, Long idWarehouse) {
        return null;
    };
    
    public default void saveDeliveryPointToFile(Controller controller, List<DeliveryPoint> listDP) throws ParserConfigurationException, SAXException, 
                                        IOException, TransformerConfigurationException, TransformerException, XPathExpressionException {    
    };
    
    public default List<DeliveryPoint> restoreDeliveryPointFromXML(Controller controller, String XMLPathMap, String XMLPathDeliveryPoint, Date planDate) 
                                                    throws ExceptionXML, ParserConfigurationException, IOException, 
                                                    SAXException, XPathExpressionException {
        return null;
    };
    
    public default Courier selectCourier(Controller controller, Long idCourier) {
        return null;
    };
    
    public default void enterDeliveryPoint(Controller controller, Map map, Long idIntersection, Long idCourier, Integer timeWindow) {
    };
    
    public default void removeDeliveryPoint(Controller controller, Map map, DeliveryPoint dp, Long idCourier){
    };

    public default void generatedDeliveryPlanForCourier(Controller controller, Courier c) {
        
    };
}
