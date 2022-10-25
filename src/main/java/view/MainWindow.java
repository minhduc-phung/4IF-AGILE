package view;

import controller.Service;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;




public class MainWindow extends Application {
    private String mapXMLPath = "maps/smallMap.xml";
    private static Intersection selectedIntersection = null;
    private static Map map = null;
    private static Pane mapPane = null;
    private static Pane interactiveBoard = null;
    private static Pane deliveryPointsInfoPane = null;
    private static ArrayList<Intersection> validatedIntersections = new ArrayList<>();
    private static Double scale;
    private static Courier selectedCourier = null;
    private static String selectedTimeWindow = null;
    private static LocalDate selectedDate = null;

    @Override
    public void start(Stage stage) throws ParserConfigurationException, IOException, SAXException {
        // PUT THE MAP XML HERE FOR NOW
        mapXMLPath = "maps/smallMap.xml";
        // Program Title
        stage.setTitle("PLD Agile");

        // Map
        mapPane = drawMapPane(mapXMLPath);

        // Interactive board
        interactiveBoard = drawInteractiveBoard();

        // Delivery points info pane
        deliveryPointsInfoPane = drawDeliveryPointsInfoPane();
        // Add containers to the window
        Group root = new Group();
        root.getChildren().add(mapPane);
        root.getChildren().add(interactiveBoard);
        root.getChildren().add(deliveryPointsInfoPane);
        Scene scene = new Scene(root, 1500, 900);
        stage.setScene(scene);
        stage.show();
    }

    private Pane drawDeliveryPointsInfoPane() {
        Pane deliveryPointsInfoPane = new Pane();
        deliveryPointsInfoPane.setPrefSize(900, 100);
        deliveryPointsInfoPane.setLayoutX(50);
        deliveryPointsInfoPane.setLayoutY(750);
        deliveryPointsInfoPane.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));

        // The frame
        Rectangle rectangle = new Rectangle(900, 100);
        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(6);
        rectangle.setFill(Color.WHITE);
        deliveryPointsInfoPane.getChildren().add(rectangle);

        // Buttons
        Button saveDeliveryPointsButton = new Button("Save delivery points");
        saveDeliveryPointsButton.setLayoutX(50);
        saveDeliveryPointsButton.setLayoutY(50);
        saveDeliveryPointsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        });
        deliveryPointsInfoPane.getChildren().add(saveDeliveryPointsButton);


        return deliveryPointsInfoPane;
    }

    private Pane drawMapPane(String mapPath) throws ParserConfigurationException, IOException, SAXException {
        Pane mapPane = new Pane();
        mapPane.setLayoutX(50);
        mapPane.setLayoutY(50);
        mapPane.setBackground(new Background(new BackgroundFill(Color.GRAY, null, null)));

        Service service = new Service();
        map = service.loadMapFromXML(mapPath);
        Double[] coords = map.getMinMaxCoordinates();
        /* Print coords
        System.out.println("minY: " + coords[0]);
        System.out.println("minX: " + coords[1]);
        System.out.println("maxY: " + coords[2]);
        System.out.println("maxX: " + coords[3]);
        System.out.println("height: " + (coords[2]-coords[0]));
        System.out.println("width: " + (coords[3]-coords[1]));
        System.out.println(map.getListIntersection().values().size());
         */
        scale = 900 / (coords[3] - coords[1]);


        // Segments
        for (Segment segment : map.getListSegment()) {
            Double posX1 = (segment.getOrigin().getLongitude() - coords[1]) * scale;
            Double posY1 = 650 - (segment.getOrigin().getLatitude() - coords[0]) * scale;
            Double posX2 = (segment.getDestination().getLongitude() - coords[1]) * scale;
            Double posY2 = 650 - (segment.getDestination().getLatitude() - coords[0]) * scale;
            Line line = new Line(posX1, posY1, posX2, posY2);
            line.setStrokeWidth(2);
            line.setStroke(Color.WHITE);
            mapPane.getChildren().add(line);
        }

        // Intersections
        for (Intersection intersection : map.getListIntersection().values()) {
            Double posX = (intersection.getLongitude() - coords[1]) * scale;
            Double posY = 650 - (intersection.getLatitude() - coords[0]) * scale;
            //System.out.println("posX: " + posX + " posY: " + posY);
            Circle point = new Circle(posX, posY, 3);
            point.setFill(Color.WHITE);
            if (Objects.equals(intersection.getId(), map.getWarehouse().getId())) {
                point.setFill(Color.RED);
                point.setRadius(4);
            }
            mapPane.getChildren().add(point);
        }

        // Events
        final Intersection[] nearestIntersection = {null};
        mapPane.setOnMouseMoved(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                nearestIntersection[0] = drawNearestPointToCursor(event, mapPane, map, scale, nearestIntersection[0]);
            }
        });

        mapPane.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                if (selectedIntersection == null) {
                    selectedIntersection = nearestIntersection[0];
                    drawIntersection(mapPane, selectedIntersection, scale, map, Color.YELLOW);
                } else {
                    drawIntersection(mapPane, selectedIntersection, scale, map, Color.WHITE);
                    selectedIntersection = nearestIntersection[0];
                    drawIntersection(mapPane, selectedIntersection, scale, map, Color.YELLOW);
                }
            }
        });

        return mapPane;
    }

    private void drawIntersection(Pane mapPane, Intersection intersection, Double scale, Map map, Color color) {
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
        // Board size and background
        interactiveBoard.setPrefSize(450, 800);
        interactiveBoard.setBackground(new Background(new BackgroundFill(Color.GRAY, null, null)));

        // The frame
        Rectangle rectangle = new Rectangle(450, 800);
        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(6);
        rectangle.setFill(Color.WHITE);
        interactiveBoard.getChildren().add(rectangle);

        // Labels
        Label label = new Label("Set up a delivery plan");
        // Center the label manually
        label.layoutXProperty().bind(interactiveBoard.widthProperty().subtract(label.widthProperty()).divide(2));
        // Change the font size
        label.setFont(new Font(20));
        // Bold
        label.setStyle("-fx-font-weight: bold");
        label.setLayoutY(50);
        Label intersectionInfo = new Label("Click on the map to select a point. Click Validate to validate the selection.");
        intersectionInfo.layoutXProperty().bind(interactiveBoard.widthProperty().subtract(intersectionInfo.widthProperty()).divide(2));
        intersectionInfo.setLayoutY(500);
        Label courierLabel = new Label("Courier");
        courierLabel.setLayoutX(10);
        courierLabel.setLayoutY(100);
        Label timeWindowLabel = new Label("Time-window");
        timeWindowLabel.setLayoutX(10);
        timeWindowLabel.setLayoutY(150);
        Label dateLabel = new Label("Date");
        dateLabel.setLayoutX(10);
        dateLabel.setLayoutY(300);
        Label infoLabel = new Label("To enter delivery points, click on an intersection on the map, or restore them from a file.");
        infoLabel.setWrapText(true);
        infoLabel.layoutXProperty().bind(interactiveBoard.widthProperty().subtract(infoLabel.widthProperty()).divide(2));
        infoLabel.setLayoutY(200);
        infoLabel.setPrefSize(375, 100);


        // ComboBox
        User user = new User();
        ComboBox couriers = new ComboBox(FXCollections.observableArrayList(user.getListCourierName()));
        couriers.setPromptText("Select a courier...");
        couriers.setLayoutX(100);
        couriers.setLayoutY(100);
        couriers.setPrefWidth(200);
        couriers.setPrefHeight(25);
        couriers.setStyle("-fx-font-size: 12px;");
        couriers.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                selectedCourier = user.getCourierByName(couriers.getValue().toString());
            }
            // TODO: update the map content to show the selected courier's chosen delivery points
        });

        String[] timeWindow = {"08:00 - 09:00", "09:00 - 10:00", "10:00 - 11:00", "11:00 - 12:00", "12:00 - 13:00", "13:00 - 14:00", "14:00 - 15:00", "15:00 - 16:00", "16:00 - 17:00"};
        ComboBox timeWindows = new ComboBox(FXCollections.observableArrayList(timeWindow));
        timeWindows.setPromptText("Select a time window...");
        timeWindows.setLayoutX(100);
        timeWindows.setLayoutY(150);
        timeWindows.setPrefWidth(200);
        timeWindows.setPrefHeight(25);
        timeWindows.setStyle("-fx-font-size: 12px;");
        timeWindows.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                selectedTimeWindow = timeWindows.getValue().toString();
            }
        });

        //DatePicker
        DatePicker datePicker = new DatePicker();
        datePicker.setLayoutX(100);
        datePicker.setLayoutY(300);
        datePicker.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                selectedDate = datePicker.getValue();
            }
        });

        // Buttons
        Button removeButton = new Button("Remove");
        removeButton.setLayoutX(75);
        removeButton.setLayoutY(400);
        removeButton.setPrefWidth(100);
        removeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (validatedIntersections.contains(selectedIntersection)) {
                    validatedIntersections.remove(selectedIntersection);
                    drawIntersection(mapPane, selectedIntersection, scale, map, Color.WHITE);
                    intersectionInfo.setText("Intersection " + selectedIntersection.getId() + " removed.");
                    selectedCourier = null;
                } else if (selectedIntersection == null) {
                    intersectionInfo.setText("No intersection selected.");
                } else {
                    intersectionInfo.setText("Intersection " + selectedIntersection.getId() + " not validated.");
                }
            }
        });
        Button validateButton = new Button("Validate");
        // Center the button manually
        //validateButton.layoutXProperty().bind(interactiveBoard.widthProperty().subtract(validateButton.widthProperty()).divide(2));
        validateButton.setLayoutX(275);
        validateButton.setLayoutY(400);
        validateButton.setPrefWidth(100);
        validateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (selectedIntersection != null) {
                    intersectionInfo.setText("Intersection " + selectedIntersection.getId() + " validated.");
                    drawIntersection(mapPane, selectedIntersection, scale, map, Color.GREEN);
                    validatedIntersections.add(selectedIntersection);
                    selectedIntersection = null;
                }
            }
        });

        Button restoreButton = new Button("Restore points");
        restoreButton.setLayoutX(285);
        restoreButton.setLayoutY(300);
        restoreButton.setPrefWidth(150);
        restoreButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        });

        Button calculateButton = new Button("Calculate the tour");
        calculateButton.setLayoutX(225);
        calculateButton.setLayoutY(750);
        calculateButton.setPrefWidth(200);
        calculateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            }
        });

        interactiveBoard.getChildren().add(label);
        interactiveBoard.getChildren().add(courierLabel);
        interactiveBoard.getChildren().add(timeWindowLabel);
        interactiveBoard.getChildren().add(dateLabel);
        interactiveBoard.getChildren().add(intersectionInfo);
        interactiveBoard.getChildren().add(infoLabel);

        interactiveBoard.getChildren().add(validateButton);
        interactiveBoard.getChildren().add(removeButton);
        interactiveBoard.getChildren().add(calculateButton);
        interactiveBoard.getChildren().add(restoreButton);

        interactiveBoard.getChildren().add(couriers);
        interactiveBoard.getChildren().add(timeWindows);
        interactiveBoard.getChildren().add(datePicker);
        return interactiveBoard;
    }

    public Intersection drawNearestPointToCursor(javafx.scene.input.MouseEvent e, Pane mapPane, Map map, Double scale, Intersection oldNearestIntersection) {
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
                drawIntersection(mapPane, nearestIntersection, scale, map, Color.ORANGE);
                //System.out.println("First time");
                //System.out.println("Nearest intersection: " + nearestIntersection.getId() + "at x: "+ point.getLayoutX() + "; y: " + point.getLayoutY());
            } else {
                if (oldNearestIntersection == selectedIntersection) {
                    drawIntersection(mapPane, oldNearestIntersection, scale, map, Color.YELLOW);
                }
                else if (validatedIntersections.contains(oldNearestIntersection)) {
                    drawIntersection(mapPane, oldNearestIntersection, scale, map, Color.GREEN);
                }  else {
                    drawIntersection(mapPane, oldNearestIntersection, scale, map, Color.WHITE);
                }
                // Remove warning "nearestIntersection might be null"
                assert nearestIntersection != null;
                drawIntersection(mapPane, nearestIntersection, scale, map, Color.ORANGE);
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
