/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.text.ParseException;


public interface Command {
    
    /**
     * Execute the command this
     */
    public void doCommand()throws ParseException;

    /**
     * Execute the reverse command of this
     */
    public void undoCommand() throws ParseException;
}
