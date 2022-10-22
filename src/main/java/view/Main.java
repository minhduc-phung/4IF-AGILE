/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.Service;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import model.Map;
import java.io.IOException;
import java.text.ParseException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import model.Courier;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author nmngo
 */
public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, ParseException {
        //testLoadMap();
        testSaveDeliveryPoints();
    }
    
    public static void testLoadMap() throws ParserConfigurationException, IOException, SAXException {
        Service service = new Service();
        Map map = service.loadMapFromXML("maps/mediumMap.xml");
    }
    
    public static void testSaveDeliveryPoints() {
        Service service = new Service();
        Courier c = new Courier(Long.parseLong("3"), "Minh");
        //String xml = service.saveDeliveryPointToFile(c);
        //System.out.println(formatXml(xml));
    }
    
    public static String formatXml(String xml) {
        try {
      
            Transformer serializer = SAXTransformerFactory.newInstance().newTransformer();
         
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
         
            Source xmlSource = new SAXSource(new InputSource(
            new ByteArrayInputStream(xml.getBytes())));
            StreamResult res =  new StreamResult(new ByteArrayOutputStream());
         
            serializer.transform(xmlSource, res);
         
            return new String(((ByteArrayOutputStream)res.getOutputStream()).toByteArray());
         
        } catch(Exception e) {
            return xml;
        }
   }
}
