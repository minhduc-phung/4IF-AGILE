package view;

import controller.Controller;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;

public class BoxListener implements EventHandler<ActionEvent> {
    private Controller controller;
    public BoxListener(Controller controller){
        this.controller = controller;
    }

    @Override
    public void handle(ActionEvent e) {
        switch (((ComboBox) e.getSource()).getId()){
            case "COURIER_BOX_ID":
                Long courierId = controller.getUser().getCourierByName((((ComboBox) e.getSource()).getValue().toString())).getId();
                controller.selectCourier(courierId);
                break;
        }
    }
}
