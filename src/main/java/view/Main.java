package view;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class Main extends Application {
    public static String mapXMLPath = "maps/smallMap.xml";
    public static InitPane initPane;
    public static MapPane mapPane;

    {
        try {
            mapPane = new MapPane(mapXMLPath);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    private static Group root;
    public static ServicePane servicePane;
    public static DeliveryPointInfoPane deliveryPointInfoPane = new DeliveryPointInfoPane();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws ParserConfigurationException, IOException, SAXException {
        String mapXMLPath = "maps/smallMap.xml";
        // Program Title
        primaryStage.setTitle("PLD Agile");

        // Init pane
        initPane = new InitPane(primaryStage);

        // Interactive board
        servicePane = new ServicePane(mapXMLPath);

        servicePane.setVisible(false);
        deliveryPointInfoPane.setVisible(false);

        // Delivery points info pane
        //deliveryPointInfoPane = new DeliveryPointInfoPane();
        // Add containers to the window
        root = new Group();
        root.getChildren().add(initPane);

        root.getChildren().add(servicePane);
        root.getChildren().add(deliveryPointInfoPane);
        Scene scene = new Scene(root, 1500, 900);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void setPanesVisible() throws ParserConfigurationException, IOException, SAXException {
        // Map
        mapPane = new MapPane(mapXMLPath);
        root.getChildren().add(mapPane);
        servicePane.setVisible(true);
        mapPane.setVisible(true);
        deliveryPointInfoPane.setVisible(true);
        initPane.setVisible(false);
    }
}
