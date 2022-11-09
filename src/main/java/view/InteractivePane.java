package view;

import controller.Controller;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.User;

import java.util.HashMap;
import java.util.Map;

public class InteractivePane extends Pane {
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

    private GraphicalView graphicalView;
    // Not to be confused with "model.Map"
    private Map<String, Button> buttons = new HashMap<String, Button>();
    private ButtonListener buttonListener;
    private MouseListener mouseListener;

    public InteractivePane(User user, Window window, Controller controller) {
        super();
        buttonListener = new ButtonListener(controller);
        mouseListener = new MouseListener(controller);
        this.setLayoutX(1050);
        this.setLayoutY(25);
        // Board size and background
        this.setPrefSize(400, 800);
        this.setBackground(new Background(new BackgroundFill(Color.FLORALWHITE, null, null)));

        // The frame
        this.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(5))));

        // Labels
        Label label = new Label("Set up a delivery plan");
        // Center the label manually
        label.layoutXProperty().bind(this.widthProperty().subtract(label.widthProperty()).divide(2));
        label.setFont(new Font(20));
        label.setStyle("-fx-font-weight: bold");
        label.setLayoutY(50);

        Label courierLabel = new Label("Courier");
        courierLabel.setLayoutX(10);
        courierLabel.setLayoutY(100);

        Label timeWindowLabel = new Label("Time-window");
        timeWindowLabel.setLayoutX(10);
        timeWindowLabel.setLayoutY(150);

        Label dateLabel = new Label("Date");
        dateLabel.setLayoutX(10);
        dateLabel.setLayoutY(300);

        // Combo box and date picker
        DatePicker datePicker = new DatePicker();
        datePicker.setId(DATE_PICKER_ID);
        datePicker.setLayoutX(110);
        datePicker.setLayoutY(300);
        datePicker.setPrefSize(200, 30);
        //datePicker.setOnAction(e -> controller.datePickerChanged(e));

        ComboBox courierBox = new ComboBox(FXCollections.observableArrayList(user.getListCourierName()));
        courierBox.setId(COURIER_BOX_ID);
        courierBox.setPlaceholder(new Label("Select a courier..."));
        courierBox.setLayoutX(100);
        courierBox.setLayoutY(100);
        courierBox.setPrefSize(200, 30);
        //courierBox.setOnAction(e -> controller.courierBoxChanged(e));

        ComboBox twBox = new ComboBox(FXCollections.observableArrayList(user.getTimeWindows().values()));
        twBox.setId(TW_BOX_ID);
        twBox.setPlaceholder(new Label("Select a time-window..."));
        twBox.setLayoutX(100);
        twBox.setLayoutY(150);
        twBox.setPrefSize(200, 30);
        //twBox.setOnAction(e -> controller.twBoxChanged(e));

        // Buttons
        Button removeButton = new Button("Remove");
        removeButton.setId(REMOVE_DP_ID);
        removeButton.setOnAction(buttonListener);
        removeButton.setLayoutX(75);
        removeButton.setLayoutY(350);
        removeButton.setPrefWidth(100);
        removeButton.setStyle("-fx-background-color: #ff9aa2; ");
        removeButton.setDisable(true);
        buttons.put(REMOVE_DP_ID, removeButton);

        Button validateButton = new Button("Validate");
        validateButton.setId(VALIDATE_DP_ID);
        validateButton.setOnAction(buttonListener);
        validateButton.setLayoutX(275);
        validateButton.setLayoutY(350);
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
        restoreButton.setLayoutX(285);
        restoreButton.setLayoutY(300);
        restoreButton.setPrefWidth(150);
        restoreButton.setDisable(true);
        restoreButton.setStyle("-fx-background-color: #c7ceea; ");
        buttons.put(RESTORE_DP_ID, restoreButton);

        Button saveButton = new Button("Save delivery points");
        saveButton.setId(SAVE_DP_ID);
        saveButton.setOnAction(buttonListener);
        validateButton.setLayoutX(1325);
        validateButton.setLayoutY(350);
        validateButton.setPrefWidth(100);
        saveButton.setStyle("-fx-background-color: #ff9aa2; ");
        saveButton.setDisable(true);
        buttons.put(SAVE_DP_ID, saveButton);

        Button calculateButton = new Button("Calculate tour");
        calculateButton.setId(CALCULATE_ID);
        calculateButton.setOnAction(buttonListener);
        calculateButton.setLayoutX(225);
        calculateButton.setLayoutY(750);
        calculateButton.setPrefWidth(200);
        calculateButton.setStyle("-fx-background-color: #c7ceea; ");
        buttons.put(CALCULATE_ID, calculateButton);

        Button loadMapButton = new Button("Load a map");
        loadMapButton.setId(LOAD_MAP_ID);
        loadMapButton.setOnAction(buttonListener);
        loadMapButton.setLayoutX(125);
        loadMapButton.setLayoutY(200);
        loadMapButton.setPrefWidth(100);
        loadMapButton.setStyle("-fx-background-color: #ff9aa2; ");
        loadMapButton.setDisable(false);
        buttons.put(LOAD_MAP_ID, loadMapButton);

        this.getChildren().add(label);
        this.getChildren().add(courierLabel);
        this.getChildren().add(timeWindowLabel);
        this.getChildren().add(dateLabel);
        this.getChildren().add(datePicker);
        this.getChildren().add(courierBox);
        this.getChildren().add(twBox);
        // Add buttons to the window
        for (Button button : buttons.values()) {
            this.getChildren().add(button);
        }

        window.getChildren().add(this);
    }
}
