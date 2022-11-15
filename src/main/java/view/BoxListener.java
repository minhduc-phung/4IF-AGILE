package view;

import controller.Controller;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;

public class BoxListener implements EventHandler<ActionEvent> {
    private Controller controller;
    public BoxListener(Controller controller){
        this.controller = controller;
    }

    @Override
    public void handle(ActionEvent e) {
        switch (((ComboBox<?>) e.getSource()).getId()){
            case "COURIER_BOX":
                // This method won't do anything if the combo box is reset when loading a new map
                if (((ComboBox<?>) e.getSource()).getSelectionModel().getSelectedItem() != null) {
                    Long courierId = controller.getUser().getCourierByName((((ComboBox<?>) e.getSource()).getValue().toString())).getId();
                    controller.selectCourier(courierId);
                }

                break;
            case "TW_BOX":
                if (((ComboBox<?>) e.getSource()).getSelectionModel().getSelectedItem() != null) {
                    Integer tw = controller.getUser().getTimeWindows().get((((ComboBox<?>) e.getSource()).getValue().toString()));
                    controller.getWindow().getInteractivePane().setSelectedTimeWindow(tw);
                    controller.getWindow().setMessage("Select an intersection to add a delivery point to the selected courier and time-window.");
                }
                // Put the chosen time window in the controller
                break;
        }
    }
}
