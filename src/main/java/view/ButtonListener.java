package view;

import controller.Controller;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;

public class ButtonListener implements EventHandler<ActionEvent> {

    private Controller controller;

    public ButtonListener(Controller controller){
            this.controller = controller;
        }

    @Override
    public void handle(ActionEvent e) {
        /*
        switch (((Node) e.getSource()).getId()){
            case "REMOVE_DP": controller.removeDeliveryPoint(); break;
            case "VALIDATE_DP": controller.validateDeliveryPoint(); break;
            case "SAVE_DP": controller.saveDeliveryPoint(); break;
            case "LOAD_DP": controller.loadDeliveryPoint(); break;

        }

         */
    }
}
