package view;

import controller.Controller;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
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

    // Combo box IDs
    protected static final String COURIER_BOX_ID = "COURIER_BOX";
    protected static final String TW_BOX_ID = "TW_BOX";

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
        removeButton.setDisable(false);
        buttons.put(REMOVE_DP_ID, removeButton);

        Button validateButton = new Button("Validate");
        validateButton.setId(VALIDATE_DP_ID);
        validateButton.setOnAction(buttonListener);
        validateButton.setLayoutX(275);
        validateButton.setLayoutY(300);
        validateButton.setPrefWidth(100);
        validateButton.setStyle("-fx-background-color: #b5ead7; ");
        validateButton.setDisable(false);
        buttons.put(VALIDATE_DP_ID, validateButton);

        Button modifyButton = new Button("Modify");
        modifyButton.setId(MODIFY_DP_ID);
        modifyButton.setOnAction(buttonListener);
        //center the button manually
        modifyButton.layoutXProperty().bind(this.widthProperty().subtract(modifyButton.widthProperty()).divide(2));
        modifyButton.setLayoutY(700);
        modifyButton.setPrefWidth(100);
        modifyButton.setDisable(false);
        buttons.put(MODIFY_DP_ID, modifyButton);

        Button generateButton = new Button("Generate delivery plan");
        generateButton.setId(GENERATE_ID);
        generateButton.setOnAction(buttonListener);
        generateButton.setLayoutX(25);
        generateButton.setLayoutY(750);
        generateButton.setPrefWidth(150);
        generateButton.setDisable(false);
        generateButton.setStyle("-fx-background-color: #c7ceea; ");
        buttons.put(GENERATE_ID, generateButton);

        Button restoreButton = new Button("Restore delivery points");
        restoreButton.setId(RESTORE_DP_ID);
        restoreButton.setOnAction(buttonListener);
        restoreButton.setLayoutX(25);
        restoreButton.setLayoutY(650);
        restoreButton.setPrefWidth(150);
        restoreButton.setDisable(false);
        restoreButton.setStyle("-fx-background-color: #c7ceea; ");
        buttons.put(RESTORE_DP_ID, restoreButton);

        Button saveButton = new Button("Save delivery points");
        saveButton.setId(SAVE_DP_ID);
        saveButton.setOnAction(buttonListener);
        saveButton.setPrefWidth(150);
        saveButton.setLayoutX(this.getPrefWidth() - saveButton.getPrefWidth() - 25);
        saveButton.setLayoutY(650);
        saveButton.setStyle("-fx-background-color: #c7ceea; ");
        saveButton.setDisable(false);
        buttons.put(SAVE_DP_ID, saveButton);

        Button calculateButton = new Button("Calculate tour");
        calculateButton.setId(CALCULATE_ID);
        calculateButton.setOnAction(buttonListener);
        calculateButton.setPrefWidth(150);
        calculateButton.setLayoutX(this.getPrefWidth() - calculateButton.getPrefWidth() - 25);
        calculateButton.setLayoutY(750);
        calculateButton.setStyle("-fx-background-color: #c7ceea; ");
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
}
