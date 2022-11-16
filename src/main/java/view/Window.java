package view;

import controller.Controller;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import model.User;

import java.util.Map;

// We have no intentions to do scaling like PlaCo example.
/**
 * this class define the size, the layout, the text and the font of all window components
 */
public class Window extends Group {
    /**
     * this attribute represents the label that shows informative messages to the user (example: <b>welcome load a map to begin....<b/>)
     */
    private Label messageFrame;

    /**
     * this attribute represents the label that shows user if a delivery point is late or not
     */
    private Label lateDeliveriesNumber;
    /**
     * this attribute represents the label for general information about the application
     */
    private Label aboutFrame;
    /**
     * this attribute represents the graphical view (map, intersections, segment) that will be shown in the window
     */
    private GraphicalView graphicalView;
    /**
     * this attribute represents all the text that will be shown in the window
     */
    private TextualView textualView;
    /**
     * this attribute represents all components (buttons, label...) and all events (buttonListener, BoxListener, and mouse listener) relative to a specific view
     */
    private InteractiveView interactiveView;
    /**
     * this attribute represents the width of the window
     */
    private final int WINDOW_WIDTH = 1500;
    /**
     * this attribute represents the height of the window
     */
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
        interactiveView = new InteractiveView(user, this, controller);
        textualView = new TextualView(this, controller);
    }

    /**
     * this method is the setter for lateDeliveriesNumber attribute it show us if there is late in deliveries or not
     *@param number represents the new value of the lateDeliveriesNumber (it's equal to 0 if there is no late)
     * @see view.Window#lateDeliveriesNumber
     */
    public void setLateDeliveriesNumber(int number) {
        if (number == 0) {
            lateDeliveriesNumber.setText("No late deliveries. Great!");
        } else {
            lateDeliveriesNumber.setText(number + " late deliveries. Not good!");
        }
    }

    /**
     * this method is for the message that will be shown to the user in the messageFrame label
     *@param message the message that will be shown to the user
     * @see view.Window#messageFrame
     */
    public void setMessage(String message) {
        messageFrame.setText(message);
    }

    /**
     * this method is the getter method for the graphicalview view attribute (map, intersections, segment that will be shown in a specific view)
     *@return the graphic that will be shown
     * @see view.Window#graphicalView
     */
    public GraphicalView getGraphicalView() {
        return graphicalView;
    }

    /**
     * this method is the getter method for the interactiveView attribute (it gets all the components (button, boxes, labels) and events relative to a specific view).
     * @return components and events to handle in the view
     * @see view.Window#interactiveView
     */
    public InteractiveView getInteractivePane() {
        return interactiveView;
    }

    /**
     * this method is the getter method for the textualView attribute (it gets all the text that will be shown in a specific view).
     * @return textualView all the text to show
     * @see view.Window#textualView
     */
    public TextualView getTextualView() {
        return textualView;
    }

    /**
     *this method allows to show or disable a component(button, combo-box) in a specific view.
     *@param nodeId the id of the component.
     *@param allow true if the node will be shown, false if not.
     */
    public void allowNode (String nodeId, boolean allow) {
        interactiveView.allowNode(nodeId, allow);

    }

    /**
     * this method shows the user if there is any late deliveries and their number if yes.
     * @param numberOfLateDeliveries
     */
    public void updateOnCalculateTour(int numberOfLateDeliveries) {
        if (numberOfLateDeliveries == 0) {
            lateDeliveriesNumber.setText("All deliveries are on time.");
            lateDeliveriesNumber.setStyle("-fx-border-color: green; -fx-border-width: 5");
            lateDeliveriesNumber.setBackground(new Background(new BackgroundFill(Color.web("0xB5EAD7"), null, null)));
            interactiveView.updateOnCalculateTour(true);
        } else {
            if (numberOfLateDeliveries == 1) {
                lateDeliveriesNumber.setText("1 late delivery.");
            } else {
                lateDeliveriesNumber.setText(numberOfLateDeliveries + " late deliveries.");
            }
            lateDeliveriesNumber.setStyle("-fx-border-color: red; -fx-border-width: 5");
            lateDeliveriesNumber.setBackground(new Background(new BackgroundFill(Color.web("0xFF9AA2"), null, null)));
            interactiveView.updateOnCalculateTour(false);
        }
    }

    /**
     * this method reset the number of late delivery and the relative button
     */
    public void resetLateDeliveryNumber() {
        lateDeliveriesNumber.setText("");
        lateDeliveriesNumber.setStyle("-fx-border-color: white; -fx-border-width: 5");
        lateDeliveriesNumber.setBackground(new Background(new BackgroundFill(Color.web("0xC3B1E1"), null, null)));
        interactiveView.resetGenerateButton();
    }
}
