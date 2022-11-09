package view;

import controller.Controller;
import javafx.collections.FXCollections;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import model.User;

import java.util.HashMap;
import java.util.Map;

// We have no intentions to do scaling like PlaCo example.
public class Window extends Group {
    // IDs of the window buttons
    protected static final String REMOVE_DP_ID = "REMOVE_DP";
    protected static final String VALIDATE_DP_ID = "VALIDATE_DP";
    protected static final String SAVE_DP_ID = "SAVE_DP";
    protected static final String RESTORE_DP_ID = "RESTORE_DP";
    protected static final String MODIFY_DP_ID = "MODIFY_DP";
    protected static final String GENERATE_ID = "GENERATE_PLAN";
    protected static final String CALCULATE_ID = "CALCULATE_TOUR";
    protected static final String LOAD_MAP_ID = "LOAD_MAP";

    // Combo box and date picker IDs
    protected static final String DATE_PICKER_ID = "DATE_PICKER";
    protected static final String COURIER_BOX_ID = "COMBO_BOX";
    protected static final String TW_BOX_ID = "TW_BOX";
    private Label messageFrame;
    private Label lateDeliveriesNumber;
    private GraphicalView graphicalView;
    // Not to be confused with "model.Map"
    private Map<String, Button> buttons = new HashMap<String, Button>();
    private ButtonListener buttonListener;
    private MouseListener mouseListener;
    private final int WINDOW_WIDTH = 1500;
    private final int WINDOW_HEIGHT = 900;


    public Window(User user, Controller controller) {
        super();
        messageFrame = new Label();
        messageFrame.setStyle("-fx-border-color: black; -fx-border-width: 5");
        messageFrame.setText("Messages will be shown here.");
        messageFrame.setPrefSize(WINDOW_WIDTH - 450, 50);
        messageFrame.setLayoutY(WINDOW_HEIGHT - 50);
        messageFrame.setLayoutX(25);
        messageFrame.setLayoutY(775);
        this.getChildren().add(messageFrame);

        lateDeliveriesNumber = new Label();
        lateDeliveriesNumber.setVisible(false);
        lateDeliveriesNumber.setStyle("-fx-border-color: black; -fx-border-width: 5");
        lateDeliveriesNumber.setText("No late deliveries. Great!");
        lateDeliveriesNumber.setPrefSize(WINDOW_WIDTH - 450, 50);
        lateDeliveriesNumber.setLayoutY(WINDOW_HEIGHT - 100);
        lateDeliveriesNumber.setLayoutX(25);
        lateDeliveriesNumber.setLayoutY(825);
        this.getChildren().add(lateDeliveriesNumber);
        graphicalView = new GraphicalView(this);

        // Combo box and date picker
        DatePicker datePicker = new DatePicker();
        datePicker.setId(DATE_PICKER_ID);
        datePicker.setLayoutX(1110);
        datePicker.setLayoutY(10);
        datePicker.setPrefSize(200, 30);
        //datePicker.setOnAction(e -> controller.datePickerChanged(e));
        this.getChildren().add(datePicker);

        // Combo box
        ComboBox courierBox = new ComboBox(FXCollections.observableArrayList(user.getListCourierName()));
        courierBox.setId(COURIER_BOX_ID);
        courierBox.setLayoutX(1110);
        courierBox.setLayoutY(50);
        courierBox.setPrefSize(200, 30);
        //courierBox.setOnAction(e -> controller.courierBoxChanged(e));
        this.getChildren().add(courierBox);

        // Combo box
        ComboBox twBox = new ComboBox(FXCollections.observableArrayList(user.getTimeWindows().values()));
        twBox.setId(TW_BOX_ID);
        twBox.setLayoutX(1110);
        twBox.setLayoutY(90);
        twBox.setPrefSize(200, 30);
        //twBox.setOnAction(e -> controller.twBoxChanged(e));
        this.getChildren().add(twBox);

        createButtons(controller);
    }

    private void createButtons(Controller controller) {
        buttonListener = new ButtonListener(controller);

        // Remove button
        Button removeButton = new Button("Remove");
        removeButton.setId(REMOVE_DP_ID);
        removeButton.setOnAction(buttonListener);
        removeButton.setLayoutX(1125);
        removeButton.setLayoutY(350);
        removeButton.setPrefWidth(100);
        removeButton.setStyle("-fx-background-color: #ff9aa2; ");
        removeButton.setDisable(true);
        buttons.put(REMOVE_DP_ID, removeButton);

        // Validate button
        Button validateButton = new Button("Validate");
        validateButton.setId(VALIDATE_DP_ID);
        validateButton.setOnAction(buttonListener);
        validateButton.setLayoutX(1325);
        validateButton.setLayoutY(350);
        validateButton.setPrefWidth(100);
        validateButton.setStyle("-fx-background-color: #ff9aa2; ");
        validateButton.setDisable(true);
        buttons.put(VALIDATE_DP_ID, validateButton);

        // Save button
        Button saveButton = new Button("Save delivery points");
        saveButton.setId(SAVE_DP_ID);
        saveButton.setOnAction(buttonListener);
        validateButton.setLayoutX(1325);
        validateButton.setLayoutY(350);
        validateButton.setPrefWidth(100);
        saveButton.setStyle("-fx-background-color: #ff9aa2; ");
        saveButton.setDisable(true);
        buttons.put(SAVE_DP_ID, saveButton);

        // Restore button
        Button restoreButton = new Button("Restore delivery points");
        restoreButton.setId(RESTORE_DP_ID);
        restoreButton.setOnAction(buttonListener);
        restoreButton.setLayoutX(1125);
        restoreButton.setLayoutY(500);
        restoreButton.setPrefWidth(150);
        restoreButton.setStyle("-fx-background-color: #ff9aa2; ");
        restoreButton.setDisable(true);
        buttons.put(RESTORE_DP_ID, restoreButton);

        // Modify button
        Button modifyButton = new Button("Modify");
        modifyButton.setId(MODIFY_DP_ID);
        modifyButton.setOnAction(buttonListener);
        modifyButton.setLayoutX(1125);
        modifyButton.setLayoutY(550);
        modifyButton.setPrefWidth(100);
        modifyButton.setStyle("-fx-background-color: #ff9aa2; ");
        modifyButton.setDisable(true);
        buttons.put(MODIFY_DP_ID, modifyButton);

        // Generate button
        Button generateButton = new Button("Generate delivery plan");
        generateButton.setId(GENERATE_ID);
        generateButton.setOnAction(buttonListener);
        generateButton.setLayoutX(1125);
        generateButton.setLayoutY(600);
        generateButton.setPrefWidth(150);
        generateButton.setStyle("-fx-background-color: #ff9aa2; ");
        generateButton.setDisable(true);
        buttons.put(GENERATE_ID, generateButton);

        // Calculate button
        Button calculateButton = new Button("Calculate tour");
        calculateButton.setId(CALCULATE_ID);
        calculateButton.setOnAction(buttonListener);
        calculateButton.setLayoutX(1125);
        calculateButton.setLayoutY(650);
        calculateButton.setPrefWidth(100);
        calculateButton.setStyle("-fx-background-color: #ff9aa2; ");
        calculateButton.setDisable(true);
        buttons.put(CALCULATE_ID, calculateButton);

        // Load map button
        Button loadMapButton = new Button("Load a map");
        loadMapButton.setId(LOAD_MAP_ID);
        loadMapButton.setOnAction(buttonListener);
        loadMapButton.setLayoutX(1125);
        loadMapButton.setLayoutY(200);
        loadMapButton.setPrefWidth(100);
        loadMapButton.setStyle("-fx-background-color: #ff9aa2; ");
        loadMapButton.setDisable(false);
        buttons.put(LOAD_MAP_ID, loadMapButton);

        // Add buttons to the window
        for (Button button : buttons.values()) {
            this.getChildren().add(button);
        }
    }

    public void setLateDeliveriesNumber(int number) {
        if (number == 0) {
            lateDeliveriesNumber.setText("No late deliveries. Great!");
        } else {
            lateDeliveriesNumber.setText(number + " late deliveries. Not good!");
        }
    }

    public void setMessage(String message) {
        messageFrame.setText(message);
    }

    public GraphicalView getGraphicalView() {
        return graphicalView;
    }
    public void drawMap(model.Map map) {
        graphicalView.drawMap(map);
        graphicalView.debug();
    }

}
