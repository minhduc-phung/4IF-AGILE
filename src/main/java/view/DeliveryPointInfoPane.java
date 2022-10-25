package view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class DeliveryPointInfoPane extends Pane {
    private final Label lateDeliveryCountLabel;
    private final Rectangle lateDeliveryPointRectangle;
    private final Button recalculateTourButton;

    public DeliveryPointInfoPane() {
        super();
        this.setPrefSize(900, 100);
        this.setLayoutX(50);
        this.setLayoutY(750);
        this.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));

        // The frame
        Rectangle rectangle = new Rectangle(900, 100);
        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(6);
        rectangle.setFill(Color.WHITE);
        this.getChildren().add(rectangle);

        // Buttons
        Button saveDeliveryPointsButton = new Button("Save delivery points");
        saveDeliveryPointsButton.setLayoutX(50);
        saveDeliveryPointsButton.setLayoutY(40);
        saveDeliveryPointsButton.setStyle("-fx-background-color: #fbc9f9; ");
        saveDeliveryPointsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        });

        recalculateTourButton = new Button("Recalculate the tour");
        recalculateTourButton.setLayoutX(500);
        recalculateTourButton.setLayoutY(40);
        recalculateTourButton.setVisible(false);
        recalculateTourButton.setStyle("-fx-background-color: #c7ceea; ");
        recalculateTourButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        });

        lateDeliveryPointRectangle = new Rectangle(120, 50);
        lateDeliveryPointRectangle.setFill(Color.web("0xFF9AA2"));
        lateDeliveryPointRectangle.setStroke(Color.RED);
        lateDeliveryPointRectangle.setStrokeWidth(3);
        lateDeliveryPointRectangle.setLayoutX(300);
        lateDeliveryPointRectangle.setLayoutY(25);
        lateDeliveryPointRectangle.setVisible(false);



        lateDeliveryCountLabel = new Label("1 late delivery");
        lateDeliveryCountLabel.setLayoutX(310);
        lateDeliveryCountLabel.setLayoutY(40);
        lateDeliveryCountLabel.setVisible(false);
        lateDeliveryCountLabel.setStyle("-fx-font-size: 16px;");
        lateDeliveryCountLabel.setTextFill(Color.WHITE);

        this.getChildren().add(saveDeliveryPointsButton);
        this.getChildren().add(recalculateTourButton);
        this.getChildren().add(lateDeliveryPointRectangle);
        this.getChildren().add(lateDeliveryCountLabel);
    }

    public void setDeliveryInfoVisible(boolean visible) {
        recalculateTourButton.setVisible(visible);
        lateDeliveryPointRectangle.setVisible(visible);
        lateDeliveryCountLabel.setVisible(visible);
    }
}

