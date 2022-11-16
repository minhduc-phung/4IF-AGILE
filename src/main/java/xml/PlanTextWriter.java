/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package xml;

import java.io.BufferedReader;
import org.w3c.dom.Document;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import model.Courier;
import model.Map;
import model.Segment;
import org.xml.sax.SAXException;


/**
 *
 * @author PQV
 */
public class PlanTextWriter {
    private static PlanTextWriter instance = null;
    private PlanTextWriter(){}
    public static PlanTextWriter getInstance() {
        if (instance == null) {
            instance = new PlanTextWriter();
        }
        return instance;
    }
        /**
     * Open an XML file and write an XML description of the plan in it 
     * @param listDP
     * @param user
     * @param plan the plan to serialise
     * @throws ParserConfigurationException
     * @throws TransformerFactoryConfigurationError
     * @throws TransformerException
     * @throws ExceptionXML
     */
    public void save(Map map, Courier courier) throws ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException, ExceptionXML, SAXException, IOException{
        File textPlan = TextFileOpener.getInstance().open(false);
        
        FileWriter writer = new FileWriter(textPlan.getAbsolutePath()); 
        BufferedReader br = new BufferedReader(new FileReader(textPlan));
        if (br.readLine() == null) {
            writer.write("For courier " + courier.getName() + ": \n");
            writeDPsToFile(map, courier, writer);
        } else {
            throw new ExceptionXML("Not an empty file");
        }
        System.out.println(courier.getName());
        System.out.println(map.getMapName());
        writer.close();
    }

    private void writeDPsToFile (Map map, Courier courier, FileWriter writer) throws IOException {       
        writer.write("From ware house, go to: \n");  
        Segment s;
        Long key = map.getWarehouse().getId();
        boolean firstElem = true;
        while (!key.equals(map.getWarehouse().getId()) || firstElem) {
            firstElem = false;
            List<Segment> listSegmentsFromDP = courier.getListSegmentCurrentTour(key);
            s = listSegmentsFromDP.get(0);
            for (int i = 1; i < listSegmentsFromDP.size(); i++) {
                Segment newSegment = listSegmentsFromDP.get(i);
                if (s.getName().equals(newSegment.getName())) {
                    s = new Segment(s.getOrigin(), newSegment.getDestination(),s.getLength()+newSegment.getLength(), s.getName());
                    continue;
                }
                writeSegmentToFile(s, writer);                
                s = listSegmentsFromDP.get(i);
            } 
            writeSegmentToFile(s, writer); 
            key = listSegmentsFromDP.get(listSegmentsFromDP.size()-1).getDestination().getId();
            if (!key.equals(map.getWarehouse().getId())) {
                writer.write("You are expected to arrive in the next delivery point at around " + courier.getTimeStampForDP(key) + " then: \n");
            }else{
                writer.write("You will arrive back at the warehouse.");
            }
            
        }
    }
    
    private void writeSegmentToFile(Segment s, FileWriter writer) throws IOException {
        writer.write("+ Take " + s.getName() + " in " + s.getLength() + " m. \n");
    }    
}
