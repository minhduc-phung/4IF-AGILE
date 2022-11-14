/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.Controller;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyEvent;

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
    public void handle(KeyEvent t) {
        controller.keystroke(t.getCode().getCode());
    }
    
}
