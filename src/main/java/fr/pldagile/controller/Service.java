/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.pldagile.controller;

import fr.pldagile.model.Intersection;
import fr.pldagile.model.Map;
import fr.pldagile.model.Segment;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document; 
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 *
 * @author nmngo
 */
public class Service {

    public Map loadMapFromXML(String XMLPath) throws ParserConfigurationException, IOException, SAXException {
        File XMLFile = new File(XMLPath);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
        DocumentBuilder dBuilder = dbf.newDocumentBuilder();
        Document doc = dBuilder.parse(XMLFile);
        doc.getDocumentElement().normalize();
        
        HashMap<Long, Intersection> listIntersection = new HashMap<>();
        NodeList nodeList = doc.getElementsByTagName("intersection");
        for (int itr = 0; itr < nodeList.getLength(); itr++) {  
            Node node = nodeList.item(itr);  
            NamedNodeMap nodeMap = node.getAttributes();
            Long idInter = Long.parseLong(nodeMap.getNamedItem("id").getNodeValue());
            Double latitude = Double.parseDouble(nodeMap.getNamedItem("latitude").getNodeValue());
            Double longitude = Double.parseDouble(nodeMap.getNamedItem("longitude").getNodeValue());
            Intersection intersection = new Intersection(idInter, latitude, longitude);
            listIntersection.put(idInter, intersection);
        }
        
        List<Segment> listSegment = new ArrayList<>();
        NodeList nodeListSeg = doc.getElementsByTagName("segment");
        for (int itr = 0; itr < nodeListSeg.getLength(); itr++) {  
            Node node = nodeListSeg.item(itr);  
            NamedNodeMap nodeMap = node.getAttributes();
            Long idOrigin = Long.parseLong(nodeMap.getNamedItem("origin").getNodeValue());
            Intersection origin = listIntersection.get(idOrigin);
            
            Long idDesti = Long.parseLong(nodeMap.getNamedItem("destination").getNodeValue());
            Intersection destination = listIntersection.get(idDesti);
            
            Double length = Double.parseDouble(nodeMap.getNamedItem("length").getNodeValue());
            String name = nodeMap.getNamedItem("name").getNodeValue();
            Segment segment = new Segment(origin, destination, length, name);
            listSegment.add(segment);  
        }
        
        Map map = new Map(listIntersection, listSegment);
        System.out.println(map.getListSegment().get(0));
        return map;
    }
    
}
