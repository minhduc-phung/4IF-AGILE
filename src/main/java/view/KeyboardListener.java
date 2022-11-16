/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.Controller;
import javafx.event.EventHandler;
import static javafx.scene.input.KeyCode.Y;
import static javafx.scene.input.KeyCode.Z;
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
    public void handle(KeyEvent e) {
        if (e.isControlDown() && e.getCode() == Z) {
            controller.undo();
        } else if (e.isControlDown() && e.getCode() == Y){
            controller.redo();
        }
    }
    
}
