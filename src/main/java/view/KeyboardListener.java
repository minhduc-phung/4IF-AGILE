/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.Controller;
import java.text.ParseException;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import static javafx.scene.input.KeyCode.CONTROL;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.Y;
import static javafx.scene.input.KeyCode.Z;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import static javafx.scene.input.KeyEvent.KEY_PRESSED;
import model.Courier;

/**
 *
 * @author bbbbb
 */
public class KeyboardListener implements EventHandler<KeyEvent> {

    private Controller controller;

    public KeyboardListener(Controller controller) {
        this.controller = controller;
        
    }

    @Override
    public void handle(KeyEvent e) {
        if (e.isControlDown() && e.getCode() == Z) {
            controller.undo();
        } else if (e.isControlDown() && e.getCode() == Y) {
            controller.redo();
        } else if (e.getCode() == ENTER) {
            controller.enterDeliveryPoint(controller.getMap(), controller.getWindow().getGraphicalView().getSelectedIntersection().getId(),
                            controller.getWindow().getInteractivePane().getSelectedCourierId(), controller.getWindow().getInteractivePane().getSelectedTimeWindow());
        }
    }
    
}
