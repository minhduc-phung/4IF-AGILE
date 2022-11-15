package view;

import controller.Controller;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.DeliveryPoint;
import model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InteractiveView extends Pane {
    // IDs of the window buttons
    protected static final String REMOVE_DP_ID = "REMOVE_DP";
    protected static final String VALIDATE_DP_ID = "VALIDATE_DP";
    protected static final String SAVE_DP_ID = "SAVE_DP";
    protected static final String RESTORE_DP_ID = "RESTORE_DP";
    protected static final String MODIFY_DP_ID = "MODIFY_DP";
    protected static final String GENERATE_ID = "GENERATE_PLAN";
    protected static final String CALCULATE_ID = "CALCULATE_TOUR";
    protected static final String LOAD_MAP_ID = "LOAD_MAP";
    protected static final String MODIFY_ENTER_DP_ID = "MODIFY_ENTER_DP";

    // Combo box IDs
    protected static final String COURIER_BOX_ID = "COURIER_BOX";
    protected static final String TW_BOX_ID = "TW_BOX";
    protected static final String ADD_DP_TO_TOUR_ID = "ADD_DP_TO_TOUR";
    protected static final String REMOVE_DP_FROM_TOUR_ID = "REMOVE_DP_FROM_TOUR";

    // Not to be confused with "model.Map"
    private Map<String, Button> buttons = new HashMap<String, Button>();
    private ComboBox<String> courierBox;
    private ComboBox<String> twBox;
    private ButtonListener buttonListener;
    private MouseListener mouseListener;
    private BoxListener boxListener;
    private Integer selectedTimeWindow;
    private Long selectedCourierId;

    public InteractiveView(User user, Window window, Controller controller) {
        super();
        buttonListener = new ButtonListener(controller);
        mouseListener = new MouseListener(controller);
        boxListener = new BoxListener(controller);
        this.setLayoutX(1050);
        this.setLayoutY(25);
        // Board size and background
        this.setPrefSize(400, 800);
        this.setBackground(new Background(new BackgroundFill(Color.FLORALWHITE, null, null)));

        // The frame
        this.setBorder(new Border(new BorderStroke(Color.SKYBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(5))));

        // Labels
        Label label = new Label("Set up a delivery plan");
        // Center the label manually
        label.layoutXProperty().bind(this.widthProperty().subtract(label.widthProperty()).divide(2));
        label.setFont(new Font(20));
        label.setStyle("-fx-font-weight: bold");
        label.setLayoutY(50);

        Label courierLabel = new Label("Courier");
        courierLabel.setLayoutX(10);
        courierLabel.setLayoutY(150);

        Label timeWindowLabel = new Label("Time-window");
        timeWindowLabel.setLayoutX(10);
        timeWindowLabel.setLayoutY(200);

        Label deliveryPointLabel = new Label("Delivery points");
        deliveryPointLabel.layoutXProperty().bind(this.widthProperty().subtract(deliveryPointLabel.widthProperty()).divide(2));
        deliveryPointLabel.setLayoutY(250);
        deliveryPointLabel.setFont(new Font(20));
        deliveryPointLabel.setStyle("-fx-font-weight: bold");

        courierBox = new ComboBox<>(FXCollections.observableArrayList(user.getListCourierName()));
        courierBox.setId(COURIER_BOX_ID);
        courierBox.setPlaceholder(new Label("Select a courier..."));
        courierBox.setDisable(true);
        courierBox.setLayoutX(175);
        courierBox.setLayoutY(150);
        courierBox.setPrefSize(200, 30);
        courierBox.setOnAction(boxListener);

        twBox = new ComboBox<>(FXCollections.observableArrayList(user.getTimeWindows().keySet()));
        twBox.setId(TW_BOX_ID);
        twBox.setPlaceholder(new Label("Select a time-window..."));
        twBox.setDisable(true);
        twBox.setLayoutX(175);
        twBox.setLayoutY(200);
        twBox.setPrefSize(200, 30);
        twBox.setOnAction(boxListener);

        // Buttons
        Button removeButton = new Button("Remove");
        removeButton.setId(REMOVE_DP_ID);
        removeButton.setOnAction(buttonListener);
        removeButton.setLayoutX(25);
        removeButton.setLayoutY(300);
        removeButton.setPrefWidth(100);
        removeButton.setStyle("-fx-background-color: #ff9aa2; ");
        removeButton.setDisable(true);
        buttons.put(REMOVE_DP_ID, removeButton);

        Button validateButton = new Button("Validate");
        validateButton.setId(VALIDATE_DP_ID);
        validateButton.setOnAction(buttonListener);
        validateButton.setLayoutX(275);
        validateButton.setLayoutY(300);
        validateButton.setPrefWidth(100);
        validateButton.setStyle("-fx-background-color: #b5ead7; ");
        validateButton.setDisable(true);
        buttons.put(VALIDATE_DP_ID, validateButton);

        Button modifyButton = new Button("Modify");
        modifyButton.setId(MODIFY_DP_ID);
        modifyButton.setOnAction(buttonListener);
        //center the button manually
        modifyButton.layoutXProperty().bind(this.widthProperty().subtract(modifyButton.widthProperty()).divide(2));
        modifyButton.setLayoutY(700);
        modifyButton.setPrefWidth(100);
        modifyButton.setDisable(true);
        buttons.put(MODIFY_DP_ID, modifyButton);
        
        Button modifyEnterButton = new Button("Enter");
        modifyEnterButton.setId(MODIFY_ENTER_DP_ID);
        modifyEnterButton.setOnAction(buttonListener);
        //center the button manually
        modifyEnterButton.layoutXProperty().bind(this.widthProperty().subtract(modifyEnterButton.widthProperty()).divide(2));
        modifyEnterButton.setLayoutY(700);
        modifyEnterButton.setPrefWidth(100);
        modifyEnterButton.setDisable(true);
        modifyEnterButton.setVisible(false);
        buttons.put(MODIFY_ENTER_DP_ID, modifyEnterButton);

        Button generateButton = new Button("Generate delivery plan");
        generateButton.setId(GENERATE_ID);
        generateButton.setOnAction(buttonListener);
        generateButton.setLayoutX(25);
        generateButton.setLayoutY(750);
        generateButton.setPrefWidth(150);
        generateButton.setDisable(true);
        generateButton.setStyle("-fx-background-color: #c7ceea; ");
        buttons.put(GENERATE_ID, generateButton);

        Button restoreButton = new Button("Restore delivery points");
        restoreButton.setId(RESTORE_DP_ID);
        restoreButton.setOnAction(buttonListener);
        restoreButton.setLayoutX(25);
        restoreButton.setLayoutY(650);
        restoreButton.setPrefWidth(150);
        restoreButton.setDisable(true);
        restoreButton.setStyle("-fx-background-color: #c7ceea; ");
        buttons.put(RESTORE_DP_ID, restoreButton);

        Button saveButton = new Button("Save delivery points");
        saveButton.setId(SAVE_DP_ID);
        saveButton.setOnAction(buttonListener);
        saveButton.setPrefWidth(150);
        saveButton.setLayoutX(this.getPrefWidth() - saveButton.getPrefWidth() - 25);
        saveButton.setLayoutY(650);
        saveButton.setStyle("-fx-background-color: #c7ceea; ");
        saveButton.setDisable(true);
        buttons.put(SAVE_DP_ID, saveButton);

        Button calculateButton = new Button("Calculate tour");
        calculateButton.setId(CALCULATE_ID);
        calculateButton.setOnAction(buttonListener);
        calculateButton.setPrefWidth(150);
        calculateButton.setLayoutX(this.getPrefWidth() - calculateButton.getPrefWidth() - 25);
        calculateButton.setLayoutY(750);
        calculateButton.setStyle("-fx-background-color: #c7ceea; ");
        calculateButton.setDisable(true);
        buttons.put(CALCULATE_ID, calculateButton);

        Button loadMapButton = new Button("Load a map");
        loadMapButton.setId(LOAD_MAP_ID);
        loadMapButton.setOnAction(buttonListener);
        loadMapButton.layoutXProperty().bind(this.widthProperty().subtract(loadMapButton.widthProperty()).divide(2));
        loadMapButton.setLayoutY(100);
        loadMapButton.setPrefWidth(100);
        loadMapButton.setStyle("-fx-background-color: #c7ceea; ");
        loadMapButton.setDisable(false);
        buttons.put(LOAD_MAP_ID, loadMapButton);

        Button addDeliveryPointToTourButton = new Button("Add");
        addDeliveryPointToTourButton.setId(ADD_DP_TO_TOUR_ID);
        addDeliveryPointToTourButton.setOnAction(buttonListener);
        addDeliveryPointToTourButton.setPrefWidth(100);
        addDeliveryPointToTourButton.setLayoutX(this.getPrefWidth() - addDeliveryPointToTourButton.getPrefWidth() - 25);
        addDeliveryPointToTourButton.setLayoutY(700);
        addDeliveryPointToTourButton.setStyle("-fx-background-color: #b5ead7; ");
        addDeliveryPointToTourButton.setVisible(false);

        Button removeDeliveryPointFromTourButton = new Button("Remove");
        removeDeliveryPointFromTourButton.setId(REMOVE_DP_FROM_TOUR_ID);
        removeDeliveryPointFromTourButton.setOnAction(buttonListener);
        removeDeliveryPointFromTourButton.setPrefWidth(100);
        removeDeliveryPointFromTourButton.setLayoutX(25);
        removeDeliveryPointFromTourButton.setLayoutY(700);
        removeDeliveryPointFromTourButton.setStyle("-fx-background-color: #ff9aa2; ");
        removeDeliveryPointFromTourButton.setVisible(false);

        this.getChildren().add(label);
        this.getChildren().add(courierLabel);
        this.getChildren().add(timeWindowLabel);
        this.getChildren().add(deliveryPointLabel);
        this.getChildren().add(courierBox);
        this.getChildren().add(twBox);
        // Add buttons to the window
        for (Button button : buttons.values()) {
            this.getChildren().add(button);
        }

        window.getChildren().add(this);
    }

    public void setSelectedCourierId(Long selectedCourierId) {
        this.selectedCourierId = selectedCourierId;
    }

    public Long getSelectedCourierId() {
        return selectedCourierId;
    }

    public Integer getSelectedTimeWindow() {
        return selectedTimeWindow;
    }

    public void setSelectedTimeWindow(Integer selectedTimeWindow) {
        this.selectedTimeWindow = selectedTimeWindow;
    }

    public void resetComboBoxes() {
        courierBox.getSelectionModel().clearSelection();
        twBox.getSelectionModel().clearSelection();
    }

    public void allowNode(String nodeId, boolean allow){
        if (Objects.equals(nodeId, COURIER_BOX_ID)){
            courierBox.setDisable(!allow);
        } else if (Objects.equals(nodeId, TW_BOX_ID)) {
            twBox.setDisable(!allow);
        } else {
            buttons.get(nodeId).setDisable(!allow);
        }
    }
    
    public void showButton(String nodeId){
        this.buttons.get(nodeId).setVisible(true);
        this.buttons.get(nodeId).setDisable(false);
    }
    
    public void hideButton(String nodeId){
        this.buttons.get(nodeId).setVisible(false);
        this.buttons.get(nodeId).setDisable(true);
    }

    public void updateOnCalculateTour(boolean allDeliveriesOnTime) {
        if (allDeliveriesOnTime){
            buttons.get(GENERATE_ID).setStyle("-fx-background-color: #b5ead7; ");
            buttons.get(GENERATE_ID).setText("Generate delivery plan");
        } else {
            buttons.get(GENERATE_ID).setStyle("-fx-background-color: #ff9aa2; ");
            buttons.get(GENERATE_ID).setText("Generate plan anyway");
        }
    }

    public void resetGenerateButton() {
        buttons.get(GENERATE_ID).setStyle("-fx-background-color: #c7ceea; ");
        buttons.get(GENERATE_ID).setText("Generate delivery plan");
    }
}
