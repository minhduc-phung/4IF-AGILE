package view;

import controller.Controller;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import model.User;

import java.util.ArrayList;

// We have no intentions to do scaling like PlaCo example.
public class Window extends Group {
    private Label messageFrame;
    private Label lateDeliveriesNumber;
    private Label aboutFrame;
    private GraphicalView graphicalView;
    private TextualView textualView;
    private InteractiveView interactiveVIew;
    private final int WINDOW_WIDTH = 1500;
    private final int WINDOW_HEIGHT = 900;


    public Window(User user, Controller controller) {
        super();
        messageFrame = new Label();
        messageFrame.setStyle("-fx-border-color: black; -fx-border-width: 5");
        messageFrame.setText("Welcome! Load a map to begin.\nMessages will be shown here.");
        messageFrame.setAlignment(Pos.CENTER);
        messageFrame.setTextAlignment(TextAlignment.CENTER);
        messageFrame.setFont(new Font(20));
        messageFrame.setPrefSize(WINDOW_WIDTH - 500, 100);
        messageFrame.setLayoutY(WINDOW_HEIGHT - 50);
        messageFrame.setLayoutX(25);
        messageFrame.setLayoutY(775);


        lateDeliveriesNumber = new Label();
        lateDeliveriesNumber.setVisible(false);
        lateDeliveriesNumber.setStyle("-fx-border-color: black; -fx-border-width: 5");
        lateDeliveriesNumber.setText("No late deliveries. Great!");
        lateDeliveriesNumber.setPrefSize(WINDOW_WIDTH - 500, 50);
        lateDeliveriesNumber.setLayoutY(WINDOW_HEIGHT - 100);
        lateDeliveriesNumber.setLayoutX(25);
        lateDeliveriesNumber.setLayoutY(825);

        aboutFrame = new Label("Long-term Project AGILE by 6-person group H4123");
        aboutFrame.setStyle("-fx-border-color: purple; -fx-border-width: 5");
        aboutFrame.setPrefSize(400, 45);
        aboutFrame.setBackground(new Background(new BackgroundFill(Color.web("0xC3B1E1"), null, null)));
        aboutFrame.setAlignment(Pos.CENTER);
        aboutFrame.setTextAlignment(TextAlignment.CENTER);
        aboutFrame.setFont(new Font("Futura", 15));
        aboutFrame.setLayoutY(WINDOW_HEIGHT - 70);
        aboutFrame.setLayoutX(WINDOW_WIDTH - 450);

        // Legend
        Pane legend = new Pane();
        legend.setPrefSize(1000, 50);
        legend.setBackground(new Background(new BackgroundFill(Color.FLORALWHITE, null, null)));
        legend.setLayoutY(730);
        legend.setLayoutX(25);

        Label legendTitle = new Label("Legend");
        legendTitle.setFont(new Font("Futura", 20));
        legendTitle.setLayoutX(5);
        //center the label
        legendTitle.layoutYProperty().bind(legend.heightProperty().subtract(legendTitle.heightProperty()).divide(2));
        legend.getChildren().add(legendTitle);

        Circle selectedIntersectionSymbol = new Circle(100, 25, 4, Color.BROWN);
        legend.getChildren().add(selectedIntersectionSymbol);
        Label selectedIntersectionLabel = new Label("Selected Intersection/Delivery Point");
        selectedIntersectionLabel.setFont(new Font("Futura", 15));
        selectedIntersectionLabel.setLayoutX(120);
        selectedIntersectionLabel.layoutYProperty().bind(legend.heightProperty().subtract(selectedIntersectionLabel.heightProperty()).divide(2));
        legend.getChildren().add(selectedIntersectionLabel);

        Circle warehouseSymbol = new Circle(400, 25, 6, Color.PURPLE);
        legend.getChildren().add(warehouseSymbol);
        Label warehouseLabel = new Label("Warehouse");
        warehouseLabel.setFont(new Font("Futura", 15));
        warehouseLabel.setLayoutX(420);
        warehouseLabel.layoutYProperty().bind(legend.heightProperty().subtract(warehouseLabel.heightProperty()).divide(2));
        legend.getChildren().add(warehouseLabel);

        Circle deliveryPointSymbol = new Circle(550, 25, 4, Color.BLUE);
        legend.getChildren().add(deliveryPointSymbol);
        Label deliveryPointLabel = new Label("Delivery Point");
        deliveryPointLabel.setFont(new Font("Futura", 15));
        deliveryPointLabel.setLayoutX(570);
        deliveryPointLabel.layoutYProperty().bind(legend.heightProperty().subtract(deliveryPointLabel.heightProperty()).divide(2));
        legend.getChildren().add(deliveryPointLabel);

        Circle onTimePointSymbol = new Circle(700, 25, 4, Color.GREEN);
        legend.getChildren().add(onTimePointSymbol);
        Label onTimePointLabel = new Label("On-time");
        onTimePointLabel.setFont(new Font("Futura", 15));
        onTimePointLabel.setLayoutX(720);
        onTimePointLabel.layoutYProperty().bind(legend.heightProperty().subtract(onTimePointLabel.heightProperty()).divide(2));
        legend.getChildren().add(onTimePointLabel);

        Circle latePointSymbol = new Circle(800, 25, 4, Color.RED);
        legend.getChildren().add(latePointSymbol);
        Label latePointLabel = new Label("Late");
        latePointLabel.setFont(new Font("Futura", 15));
        latePointLabel.setLayoutX(820);
        latePointLabel.layoutYProperty().bind(legend.heightProperty().subtract(latePointLabel.heightProperty()).divide(2));
        legend.getChildren().add(latePointLabel);









        this.getChildren().add(legend);
        this.getChildren().add(messageFrame);
        this.getChildren().add(aboutFrame);
        this.getChildren().add(lateDeliveriesNumber);
        graphicalView = new GraphicalView(this, controller);
        interactiveVIew = new InteractiveView(user, this, controller);
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

    public InteractiveView getInteractivePane() {
        return interactiveVIew;
    }

    public void allowNode (String nodeId, boolean allow) {
        interactiveVIew.allowNode(nodeId, allow);
    }
}
