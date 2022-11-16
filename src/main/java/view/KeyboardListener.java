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
 *This class is the listener for the keyboard events on the graphical view and the textual view.
 * It allows the user to undo and redo using the keyboard (ctrl + z/ ctrl + y).
 */
public class KeyboardListener implements EventHandler<KeyEvent> {

    private Controller controller;

    public KeyboardListener(Controller controller) {
        this.controller = controller;
        
    }

    /**
     * this method handles the action to do after the incoming Event.
     *
     * @param e represents the incoming keyboard event
     */
    @Override
    public void handle(KeyEvent e) {
        if (e.isControlDown() && e.getCode() == Z) {
            controller.undo();
        } else if (e.isControlDown() && e.getCode() == Y){
            controller.redo();
        }
    }
    
}
