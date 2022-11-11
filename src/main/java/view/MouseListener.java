package view;

import controller.Controller;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

// This class is the listener for the mouse events on the graphical view
public class MouseListener implements EventHandler<MouseEvent> {
    private Controller controller;

    public MouseListener(Controller c){
        this.controller = c;
    }

    @Override
    public void handle(MouseEvent mouseEvent) {
        switch (mouseEvent.getEventType().getName()){
            case "MOUSE_CLICKED":
                controller.mouseClickedOnMap();
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
