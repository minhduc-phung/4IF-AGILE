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
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.Intersection;
import model.Map;
import model.Segment;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Objects;




public class Main extends Application {
    private static Intersection clickedIntersection = null;
    @Override
    public void start(Stage stage) throws ParserConfigurationException, IOException, SAXException {
        // PUT THE MAP XML HERE FOR NOW
        String mapXMLPath = "maps/smallMap.xml";
        stage.setTitle("PLD Agile");

        // Map
        Pane mapPane = drawMapPane(mapXMLPath);

        // Interactive board
        Pane interactiveBoard = drawInteractiveBoard();

        // Label example
        Label label = new Label(mapXMLPath);
        label.setLayoutX(50);
        label.setLayoutY(25);

        // Add containers to the window
        Group root = new Group();
        root.getChildren().add(mapPane);
        root.getChildren().add(interactiveBoard);
        root.getChildren().add(label);
        Scene scene = new Scene(root, 1500, 900);
        stage.setScene(scene);
        stage.show();
    }

    private Pane drawMapPane(String mapPath) throws ParserConfigurationException, IOException, SAXException {

        Pane mapPane = new Pane();
        mapPane.setLayoutX(50);
        mapPane.setLayoutY(50);
        mapPane.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.GRAY, null, null)));

        Service service = new Service();
        Map map = service.loadMapFromXML(mapPath);
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
        Double scale = 900 / (coords[3] - coords[1]);


        // Segments
        for (Segment segment : map.getListSegment()) {
            Double posX1 = (segment.getOrigin().getLongitude() - coords[1]) * scale;
            Double posY1 = 650 - (segment.getOrigin().getLatitude() - coords[0]) * scale;
            Double posX2 = (segment.getDestination().getLongitude() - coords[1]) * scale;
            Double posY2 = 650 - (segment.getDestination().getLatitude() - coords[0]) * scale;
            Line line = new Line(posX1, posY1, posX2, posY2);
            line.setStrokeWidth(2);
            line.setStroke(javafx.scene.paint.Color.WHITE);
            mapPane.getChildren().add(line);
        }

        // Intersections
        for (Intersection intersection : map.getListIntersection().values()) {
            Double posX = (intersection.getLongitude() - coords[1]) * scale;
            Double posY = 650 - (intersection.getLatitude() - coords[0]) * scale;
            //System.out.println("posX: " + posX + " posY: " + posY);
            Circle point = new Circle(posX, posY, 3);
            point.setFill(javafx.scene.paint.Color.RED);
            if (Objects.equals(intersection.getId(), map.getWarehouse().getId())) {
                point.setFill(javafx.scene.paint.Color.BLUE);
                point.setRadius(4);
            }
            mapPane.getChildren().add(point);
        }

        // Events
        final Intersection[] nearestIntersection = {null};
        mapPane.setOnMouseMoved(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                nearestIntersection[0] = getNearestPoint(event, mapPane, map, scale, nearestIntersection[0]);
            }
        });

        mapPane.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                if (nearestIntersection[0] != null) {
                    clickedIntersection = nearestIntersection[0];
                }
            }
        });

        return mapPane;
    }

    private void drawIntersection(Pane mapPane, Intersection intersection, Double scale, Map map, javafx.scene.paint.Color color) {
        Double posX = (intersection.getLongitude() - map.getMinMaxCoordinates()[1]) * scale;
        Double posY = 650 - (intersection.getLatitude() - map.getMinMaxCoordinates()[0]) * scale;
        Circle point = new Circle(posX, posY, 3);
        point.setFill(color);
        mapPane.getChildren().add(point);
    }

    private Pane drawInteractiveBoard(){
        Pane interactiveBoard = new Pane();
        interactiveBoard.setLayoutX(1000);
        interactiveBoard.setLayoutY(50);
        // Set size of the board
        interactiveBoard.setPrefSize(400, 800);
        // Set background color
        interactiveBoard.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.GRAY, null, null)));
        // Draw the frame
        Rectangle rectangle = new Rectangle(400, 800);
        rectangle.setStroke(javafx.scene.paint.Color.BLACK);
        rectangle.setStrokeWidth(10);
        rectangle.setFill(javafx.scene.paint.Color.WHITE);
        interactiveBoard.getChildren().add(rectangle);

        Label label = new Label("Interactive board");
        // Center the label manually
        label.layoutXProperty().bind(interactiveBoard.widthProperty().subtract(label.widthProperty()).divide(2));
        // Change the font size
        label.setFont(new Font(20));
        // Bold
        label.setStyle("-fx-font-weight: bold");
        label.setLayoutY(50);


        Label intersectionInfo = new Label("Click on the map and click validate to select a point.");
        intersectionInfo.layoutXProperty().bind(interactiveBoard.widthProperty().subtract(intersectionInfo.widthProperty()).divide(2));
        intersectionInfo.setLayoutY(500);

        // Add a button in the middle
        Button button = new Button("Validate");
        // Center the button manually
        button.layoutXProperty().bind(interactiveBoard.widthProperty().subtract(button.widthProperty()).divide(2));
        button.setLayoutY(400);
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (clickedIntersection != null) {
                    intersectionInfo.setText("Intersection " + clickedIntersection.getId() + " selected.");
                }
            }
        });

        interactiveBoard.getChildren().add(label);
        interactiveBoard.getChildren().add(intersectionInfo);
        interactiveBoard.getChildren().add(button);
        return interactiveBoard;
    }

    public Intersection getNearestPoint(javafx.scene.input.MouseEvent e, Pane mapPane, Map map, Double scale, Intersection oldNearestIntersection) {
        double x = e.getX();
        double y = e.getY();
        double minDistance = 1000000000;
        Intersection nearestIntersection = null;
        for (Intersection intersection : map.getListIntersection().values()) {
            if (Objects.equals(intersection.getId(), map.getWarehouse().getId())) {
                continue;
            }
            Double posX = (intersection.getLongitude() - map.getMinMaxCoordinates()[1]) * scale;
            Double posY = 650 - (intersection.getLatitude() - map.getMinMaxCoordinates()[0]) * scale;
            double distance = Math.sqrt(Math.pow(posX - x, 2) + Math.pow(posY - y, 2));

            if (distance < minDistance) {
                minDistance = distance;
                nearestIntersection = intersection;
            }
        }
        if (oldNearestIntersection != nearestIntersection) {
            // If it's the first time the mouse enter the map
            if (oldNearestIntersection == null) {
                drawIntersection(mapPane, nearestIntersection, scale, map, javafx.scene.paint.Color.GREEN);
                //System.out.println("First time");
                //System.out.println("Nearest intersection: " + nearestIntersection.getId() + "at x: "+ point.getLayoutX() + "; y: " + point.getLayoutY());
            } else {
                drawIntersection(mapPane, oldNearestIntersection, scale, map, javafx.scene.paint.Color.RED);
                // Remove warning "nearestIntersection might be null"
                assert nearestIntersection != null;
                drawIntersection(mapPane, nearestIntersection, scale, map, javafx.scene.paint.Color.GREEN);
                //System.out.println("Old nearest intersection: " + oldNearestIntersection.getId() + "at x: "+ oldPoint.getCenterX() + "; y: " + oldPoint.getCenterY());
                //System.out.println("Nearest intersection: " + nearestIntersection.getId() + " at x: "+ point.getCenterX() + "; y: " + point.getCenterY());
            }
        }
        return nearestIntersection;
    }
    public static void main(String[] args) {
        launch();
    }
}
