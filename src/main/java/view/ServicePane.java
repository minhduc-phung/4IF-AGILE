package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import model.Courier;
import model.Intersection;
import model.User;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class ServicePane extends Pane {
    private Courier selectedCourier = null;
    private String selectedTimeWindow = null;
    private LocalDate selectedDate = null;

    public ServicePane(String mapPath) throws ParserConfigurationException, IOException, SAXException {
        super();
        DeliveryPointInfoPane deliveryPointInfoPane = Main.deliveryPointInfoPane;
        // Input example data, to be replaced with actual classes in the model

        String[] timeWindow = {"08:00 - 09:00", "09:00 - 10:00", "10:00 - 11:00", "11:00 - 12:00", "12:00 - 13:00", "13:00 - 14:00", "14:00 - 15:00", "15:00 - 16:00", "16:00 - 17:00"};
        ObservableList<SimpleExampleData> data = SimpleExampleData.InitData();
        this.setLayoutX(1000);
        this.setLayoutY(50);
        // Board size and background
        this.setPrefSize(450, 800);
        this.setBackground(new Background(new BackgroundFill(Color.GRAY, null, null)));

        // The frame
        Rectangle rectangle = new Rectangle(450, 800);
        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(6);
        rectangle.setFill(Color.web("0xFFDAC1"));
        this.getChildren().add(rectangle);

        // Labels
        Label label = new Label("Set up a delivery plan");
        // Center the label manually
        label.layoutXProperty().bind(this.widthProperty().subtract(label.widthProperty()).divide(2));
        label.setFont(new Font(20));
        label.setStyle("-fx-font-weight: bold");
        label.setLayoutY(50);

        Label intersectionInfo = new Label("Click on the map to select a point. Click Validate to validate the selection.");
        intersectionInfo.layoutXProperty().bind(this.widthProperty().subtract(intersectionInfo.widthProperty()).divide(2));
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
        infoLabel.layoutXProperty().bind(this.widthProperty().subtract(infoLabel.widthProperty()).divide(2));
        infoLabel.setLayoutY(200);
        infoLabel.setPrefSize(375, 100);

        Label calculatedInfoLabel = new Label("You can modify the tour by clicking the button below and then click on the map to add or remove delivery points.");
        calculatedInfoLabel.setWrapText(true);
        calculatedInfoLabel.layoutXProperty().bind(this.widthProperty().subtract(calculatedInfoLabel.widthProperty()).divide(2));
        calculatedInfoLabel.setLayoutY(600);
        calculatedInfoLabel.setPrefSize(375, 100);
        calculatedInfoLabel.setVisible(false);

        // Buttons
        Button removeButton = new Button("Remove");
        removeButton.setLayoutX(75);
        removeButton.setLayoutY(350);
        removeButton.setPrefWidth(100);
        removeButton.setStyle("-fx-background-color: #ff9aa2; ");
        removeButton.setDisable(true);
        removeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ArrayList<Intersection> validatedIntersections = Main.mapPane.getValidatedIntersections();
                Intersection selectedIntersection = Main.mapPane.getSelectedIntersection();
                if (validatedIntersections.contains(selectedIntersection)) {
                    validatedIntersections.remove(selectedIntersection);
                    Main.mapPane.drawIntersection(selectedIntersection, Color.WHITE);
                    intersectionInfo.setText("Intersection " + selectedIntersection.getId() + " removed.");
                    selectedCourier = null;
                } else if (selectedIntersection == null) {
                    intersectionInfo.setText("No intersection selected.");
                } else {
                    intersectionInfo.setText("Intersection " + selectedIntersection.getId() + " was not yet validated.");
                }
            }
        });

        Button validateButton = new Button("Validate");
        validateButton.setLayoutX(275);
        validateButton.setLayoutY(350);
        validateButton.setPrefWidth(100);
        validateButton.setStyle("-fx-background-color: #b5ead7; ");
        validateButton.setDisable(true);
        validateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Intersection selectedIntersection = Main.mapPane.getSelectedIntersection();
                System.out.println(selectedIntersection);
                System.out.println(Main.mapPane.getSelectedIntersection());
                if (selectedIntersection != null) {
                    intersectionInfo.setText("Intersection " + selectedIntersection.getId() + " validated.");
                    Main.mapPane.drawIntersection(selectedIntersection, Color.GREEN);
                    Main.mapPane.addValidatedIntersection(selectedIntersection);
                    Main.mapPane.setSelectedIntersection(null);
                }
            }
        });

        Button modifyButton = new Button("Modify");
        //center the button manually
        modifyButton.layoutXProperty().bind(this.widthProperty().subtract(modifyButton.widthProperty()).divide(2));
        modifyButton.setLayoutY(700);
        modifyButton.setPrefWidth(100);
        modifyButton.setDisable(true);
        modifyButton.setVisible(false);
        modifyButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Main.mapPane.setDisable(false);
                modifyButton.setVisible(false);
                calculatedInfoLabel.setVisible(true);
                removeButton.setDisable(false);
                validateButton.setDisable(false);
            }
        });

        Button generatePlanButton = new Button("Generate delivery plan");
        generatePlanButton.setLayoutX(25);
        generatePlanButton.setLayoutY(750);
        generatePlanButton.setPrefWidth(150);
        generatePlanButton.setDisable(true);
        generatePlanButton.setStyle("-fx-background-color: #c7ceea; ");

        // ComboBox
        ComboBox timeWindows = new ComboBox(FXCollections.observableArrayList
                (timeWindow));
        timeWindows.setPromptText("Select a time window...");
        timeWindows.setLayoutX(100);
        timeWindows.setLayoutY(150);
        timeWindows.setPrefWidth(200);
        timeWindows.setPrefHeight(25);
        timeWindows.setDisable(true);
        timeWindows.setStyle("-fx-font-size: 12px;");
        timeWindows.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                selectedTimeWindow = timeWindows.getValue().toString();
                removeButton.setDisable(false);
                validateButton.setDisable(false);
                Main.mapPane.setDisable(false);
            }
        });

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
                timeWindows.setDisable(false);
            }
            // TODO: update the map content to show the selected courier's chosen delivery points
        });






        //DatePicker for restoring points
        Button restoreButton = new Button("Restore points");
        restoreButton.setLayoutX(285);
        restoreButton.setLayoutY(300);
        restoreButton.setPrefWidth(150);
        restoreButton.setDisable(true);
        restoreButton.setStyle("-fx-background-color: #c7ceea; ");
        restoreButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        });

        DatePicker datePicker = new DatePicker();
        datePicker.setLayoutX(100);
        datePicker.setLayoutY(300);
        datePicker.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                selectedDate = datePicker.getValue();
                restoreButton.setDisable(false);
            }
        });






        // TableView for the plan
        TableView<SimpleExampleData> table = new TableView<>();
        table.setVisible(false);
        table.setEditable(false);
        TableColumn timeWindowColumn = new TableColumn("Time-window");
        timeWindowColumn.setCellValueFactory(new PropertyValueFactory<>("timeWindow"));
        timeWindowColumn.setMinWidth(50);

        TableColumn dpColumn = new TableColumn("Delivery point");
        dpColumn.setCellValueFactory(new PropertyValueFactory<>("deliveryPoint"));
        dpColumn.setMinWidth(50);

        TableColumn timeColumn = new TableColumn("Est. arrival");
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        timeColumn.setMinWidth(50);

        table.setItems(data);
        table.getColumns().addAll(timeWindowColumn, dpColumn, timeColumn);
        table.setLayoutX(25);
        table.setLayoutY(400);
        table.setPrefWidth(400);
        table.setPrefHeight(200);
        table.setStyle("-fx-font-size: 12px;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Button calculateButton = new Button("Calculate the tour");
        calculateButton.setLayoutX(225);
        calculateButton.setLayoutY(750);
        calculateButton.setPrefWidth(200);
        calculateButton.setStyle("-fx-background-color: #c7ceea; ");
        calculateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                table.setVisible(true);
                calculatedInfoLabel.setVisible(true);
                calculateButton.setDisable(false);
                generatePlanButton.setDisable(false);
                deliveryPointInfoPane.setDeliveryInfoVisible(true);
                removeButton.setDisable(true);
                validateButton.setDisable(true);
                Main.mapPane.setDisable(true);
                modifyButton.setVisible(true);
                modifyButton.setDisable(false);
            }
        });


        this.getChildren().add(label);
        this.getChildren().add(courierLabel);
        this.getChildren().add(timeWindowLabel);
        this.getChildren().add(dateLabel);
        this.getChildren().add(intersectionInfo);
        this.getChildren().add(infoLabel);
        this.getChildren().add(calculatedInfoLabel);

        this.getChildren().add(validateButton);
        this.getChildren().add(removeButton);
        this.getChildren().add(calculateButton);
        this.getChildren().add(restoreButton);
        this.getChildren().add(generatePlanButton);
        this.getChildren().add(modifyButton);

        this.getChildren().add(couriers);
        this.getChildren().add(timeWindows);
        this.getChildren().add(datePicker);

        this.getChildren().add(table);
    }
}
