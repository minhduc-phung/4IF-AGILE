/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;


public interface Command {
    
    /**
     * Execute the command this
     */
    public void doCommand();

    /**
     * Execute the reverse command of this
     */
    public void undoCommand();
}
