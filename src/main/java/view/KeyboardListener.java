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
            Long idCourier = controller.getWindow().getInteractivePane().getSelectedCourierId();
            Courier c = controller.getUser().getCourierById(idCourier);
            
            controller.undo();
        }
    }
    
}
