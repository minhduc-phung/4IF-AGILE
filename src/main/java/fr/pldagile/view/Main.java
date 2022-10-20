/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.pldagile.view;

import fr.pldagile.controller.Service;
import fr.pldagile.model.Map;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author nmngo
 */
public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        Service service = new Service();
        Map map = service.loadMapFromXML("maps/mediumMap.xml");
    }
}
