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
    private Label messageFrame;
    private Label lateDeliveriesNumber;
    private GraphicalView graphicalView;
    private TextualView textualView;
    private InteractivePane interactivePane;
    private final int WINDOW_WIDTH = 1500;
    private final int WINDOW_HEIGHT = 900;


    public Window(User user, Controller controller) {
        super();
        messageFrame = new Label();
        messageFrame.setStyle("-fx-border-color: black; -fx-border-width: 5");
        messageFrame.setText("Messages will be shown here.");
        messageFrame.setPrefSize(WINDOW_WIDTH - 500, 50);
        messageFrame.setLayoutY(WINDOW_HEIGHT - 50);
        messageFrame.setLayoutX(25);
        messageFrame.setLayoutY(775);
        this.getChildren().add(messageFrame);

        lateDeliveriesNumber = new Label();
        lateDeliveriesNumber.setVisible(false);
        lateDeliveriesNumber.setStyle("-fx-border-color: black; -fx-border-width: 5");
        lateDeliveriesNumber.setText("No late deliveries. Great!");
        lateDeliveriesNumber.setPrefSize(WINDOW_WIDTH - 500, 50);
        lateDeliveriesNumber.setLayoutY(WINDOW_HEIGHT - 100);
        lateDeliveriesNumber.setLayoutX(25);
        lateDeliveriesNumber.setLayoutY(825);
        this.getChildren().add(lateDeliveriesNumber);
        graphicalView = new GraphicalView(this, controller);
        interactivePane = new InteractivePane(user, this, controller);
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

}
