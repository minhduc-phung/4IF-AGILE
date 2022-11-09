package view;

import controller.Controller;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import org.xml.sax.SAXException;
import xml.ExceptionXML;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class ButtonListener implements EventHandler<ActionEvent> {

    private Controller controller;

    public ButtonListener(Controller controller){
            this.controller = controller;
        }

    @Override
    public void handle(ActionEvent e) {

        switch (((Node) e.getSource()).getId()){
            case "LOAD_MAP":
                try {
                    controller.loadMapFromXML();
                } catch (ParserConfigurationException | IOException | SAXException | ExceptionXML ex) {
                    throw new RuntimeException(ex);
                }
                break;
        }


    }
}
