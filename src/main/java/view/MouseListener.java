package view;

import controller.Controller;
import javafx.event.EventHandler;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;

// This class is the listener for the mouse events on the graphical view and the textual view
public class MouseListener implements EventHandler<MouseEvent> {
    private Controller controller;

    public MouseListener(Controller c){
        this.controller = c;
    }

    @Override
    public void handle(MouseEvent mouseEvent) {
        switch (mouseEvent.getEventType().getName()){
            case "MOUSE_CLICKED":
                if (mouseEvent.getSource() instanceof GraphicalView){
                    controller.mouseClickedOnMap();
                } else {
                    controller.mouseClickedOnTable(((TableRow) mouseEvent.getSource()).getIndex()+1);
                }
                break;
            case "MOUSE_MOVED":
                controller.mouseMovedOnMap(mouseEvent.getX(), mouseEvent.getY());
                break;
            case "MOUSE_EXITED":
                controller.mouseExitedMap();
                break;
        }
    }
}
