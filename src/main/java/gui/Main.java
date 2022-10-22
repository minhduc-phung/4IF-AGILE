package gui;

import controller.Service;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import model.Intersection;
import model.Map;
import model.Segment;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Objects;




public class Main extends Application {
    @Override
    public void start(Stage stage) throws ParserConfigurationException, IOException, SAXException {
        // PUT THE MAP XML HERE FOR NOW
        String mapXMLPath = "maps/smallMap.xml";
        stage.setTitle("PLD Agile");

        /// The Map ///

        // Intersections

        // Container for the map
        Pane mapPane = new Pane();
        mapPane.setLayoutX(50);
        mapPane.setLayoutY(50);
        mapPane.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.GRAY, null, null)));

        Service service = new Service();
        Map map = service.loadMapFromXML(mapXMLPath);
        Double[] coords = map.getMinMaxCoordinates();
        /* print out the coords
        System.out.println("minY: " + coords[0]);
        System.out.println("minX: " + coords[1]);
        System.out.println("maxY: " + coords[2]);
        System.out.println("maxX: " + coords[3]);
        System.out.println("height: " + (coords[2]-coords[0]));
        System.out.println("width: " + (coords[3]-coords[1]));
        System.out.println(map.getListIntersection().values().size());
         */
        Double scale = 1000/(coords[3]-coords[1]);


        // Segments
        for(Segment segment : map.getListSegment()) {
            Double posX1 = (segment.getOrigin().getLongitude()-coords[1])*scale;
            Double posY1 = 750-(segment.getOrigin().getLatitude()-coords[0])*scale;
            Double posX2 = (segment.getDestination().getLongitude()-coords[1])*scale;
            Double posY2 = 750-(segment.getDestination().getLatitude()-coords[0])*scale;
            Line line = new Line(posX1, posY1, posX2, posY2);
            line.setStrokeWidth(2);
            line.setStroke(javafx.scene.paint.Color.WHITE);
            mapPane.getChildren().add(line);
        }

        // Intersections
        for(Intersection intersection : map.getListIntersection().values()) {
            Double posX = (intersection.getLongitude()-coords[1])*scale;
            Double posY = 750-(intersection.getLatitude()-coords[0])*scale;
            System.out.println("posX: " + posX+ " posY: " + posY);
            Circle point = new Circle(posX, posY, 2);
            point.setFill(javafx.scene.paint.Color.BLUE);
            if (Objects.equals(intersection.getId(), map.getWarehouse().getId())) {
                point.setFill(javafx.scene.paint.Color.RED);
                point.setRadius(4);
            }
            mapPane.getChildren().add(point);
        }

        // Label example
        Label label = new Label(mapXMLPath);
        label.setLayoutX(50);
        label.setLayoutY(25);

        // Button example for future coding (it does nothing)
        Button button = new Button();
        button.setText("This button does nothing for now");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("I do nothing.");
            }
        });
        button.setLayoutX(750 - button.getWidth()/2);
        button.setLayoutY(50);

        // Container for the button and the text
        Pane buttonPane = new Pane();
        buttonPane.setLayoutY(900);
        buttonPane.getChildren().add(button);

        // Add containers to the window
        Group root = new Group();
        root.getChildren().add(buttonPane);
        root.getChildren().add(mapPane);
        root.getChildren().add(label);
        Scene scene = new Scene(root, 1500, 1000);
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}
