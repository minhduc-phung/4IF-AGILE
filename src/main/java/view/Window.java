package view;

import controller.Controller;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

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

    // Titles of window buttons
    // Probably useless, but kept for now
    protected final static String REMOVE_DP = "Remove";
    protected static final String VALIDATE_DP = "Validate";
    protected static final String SAVE_DP = "Save the delivery points";
    protected static final String RESTORE_DP = "Restore delivery points";
    protected static final String MODIFY_DP = "Modify";
    protected static final String GENERATE = "Generate delivery plan";
    protected static final String CALCULATE = "Calculate tour";
    protected static final String LOAD_MAP = "Load a map";

    // Combo box and date picker IDs
    protected static final String DATE_PICKER_ID = "DATE_PICKER";
    protected static final String COMBO_BOX_ID = "COMBO_BOX";

    private Group root;
    private Label messageFrame;
    private Label lateDeliveriesNumber;
    private GraphicalView graphicalView;
    // Not to be confused with "model.Map"
    private Map<String, Button> buttons = new HashMap<String, Button>();
    private ButtonListener buttonListener;
    //private MouseListener mouseListener;
    private final int WINDOW_WIDTH = 1500;
    private final int WINDOW_HEIGHT = 900;


    public Window(Controller controller) {
        super();
        root = new Group();
//        this.setWidth(WINDOW_WIDTH);
//        this.setHeight(WINDOW_HEIGHT);
//        this.setTitle("Delivery Planner");
//        this.setResizable(false);
//        this.setOnCloseRequest(e -> System.exit(0));
        messageFrame = new Label();
        //messageFrame.setBorder(Border.stroke(Paint.valueOf("black")));
        messageFrame.setStyle("ffx-border-color: black; -fx-border-width: 5");
        messageFrame.setText("Messages will be shown here.");
        messageFrame.setPrefSize(WINDOW_WIDTH, 50);
        messageFrame.setLayoutY(WINDOW_HEIGHT - 50);

        lateDeliveriesNumber = new Label();
        lateDeliveriesNumber.setStyle("ffx-border-color: black; -fx-border-width: 5");
        lateDeliveriesNumber.setText("No late deliveries. Great!");
        lateDeliveriesNumber.setPrefSize(WINDOW_WIDTH, 50);
        lateDeliveriesNumber.setLayoutY(WINDOW_HEIGHT - 100);
        graphicalView = new GraphicalView(this);

        createButtons(controller);
    }

    private void createButtons(Controller controller) {
        buttonListener = new ButtonListener(controller);

        // Remove button
        Button removeButton = new Button("Remove");
        removeButton.setId(REMOVE_DP_ID);
        removeButton.setOnAction(buttonListener);
        removeButton.setLayoutX(75);
        removeButton.setLayoutY(350);
        removeButton.setPrefWidth(100);
        removeButton.setStyle("-fx-background-color: #ff9aa2; ");
        removeButton.setDisable(true);
        buttons.put(REMOVE_DP_ID, removeButton);

        // Validate button
        Button validateButton = new Button("Validate");
        validateButton.setId(VALIDATE_DP_ID);
        validateButton.setOnAction(buttonListener);
        validateButton.setLayoutX(275);
        validateButton.setLayoutY(350);
        validateButton.setPrefWidth(100);
        validateButton.setStyle("-fx-background-color: #ff9aa2; ");
        validateButton.setDisable(true);
        buttons.put(VALIDATE_DP_ID, validateButton);

        // Save button
        Button saveButton = new Button("Save delivery points");
        saveButton.setId(SAVE_DP_ID);
        saveButton.setOnAction(buttonListener);
        saveButton.setLayoutX(75);
        saveButton.setLayoutY(450);
        saveButton.setPrefWidth(100);
        saveButton.setStyle("-fx-background-color: #ff9aa2; ");
        saveButton.setDisable(true);
        buttons.put(SAVE_DP_ID, saveButton);

        // Restore button
        Button restoreButton = new Button("Restore delivery points");
        restoreButton.setId(RESTORE_DP_ID);
        restoreButton.setOnAction(buttonListener);
        restoreButton.setLayoutX(75);
        restoreButton.setLayoutY(500);
        restoreButton.setPrefWidth(100);
        restoreButton.setStyle("-fx-background-color: #ff9aa2; ");
        restoreButton.setDisable(true);
        buttons.put(RESTORE_DP_ID, restoreButton);

        // Modify button
        Button modifyButton = new Button("Modify");
        modifyButton.setId(MODIFY_DP_ID);
        modifyButton.setOnAction(buttonListener);
        modifyButton.setLayoutX(75);
        modifyButton.setLayoutY(550);
        modifyButton.setPrefWidth(100);
        modifyButton.setStyle("-fx-background-color: #ff9aa2; ");
        modifyButton.setDisable(true);
        buttons.put(MODIFY_DP_ID, modifyButton);

        // Generate button
        Button generateButton = new Button("Generate delivery plan");
        generateButton.setId(GENERATE_ID);
        generateButton.setOnAction(buttonListener);
        generateButton.setLayoutX(75);
        generateButton.setLayoutY(600);
        generateButton.setPrefWidth(100);
        generateButton.setStyle("-fx-background-color: #ff9aa2; ");
        generateButton.setDisable(true);
        buttons.put(GENERATE_ID, generateButton);

        // Calculate button
        Button calculateButton = new Button("Calculate tour");
        calculateButton.setId(CALCULATE_ID);
        calculateButton.setOnAction(buttonListener);
        calculateButton.setLayoutX(75);
        calculateButton.setLayoutY(650);
        calculateButton.setPrefWidth(100);
        calculateButton.setStyle("-fx-background-color: #ff9aa2; ");
        calculateButton.setDisable(true);
        buttons.put(CALCULATE_ID, calculateButton);

        // Load map button
        Button loadMapButton = new Button("Load a map");
        loadMapButton.setId(LOAD_MAP_ID);
        loadMapButton.setOnAction(buttonListener);
        loadMapButton.setLayoutX(75);
        loadMapButton.setLayoutY(700);
        loadMapButton.setPrefWidth(100);
        loadMapButton.setStyle("-fx-background-color: #ff9aa2; ");
        loadMapButton.setDisable(true);
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

    public Group getRoot() {
        return root;
    }
}
