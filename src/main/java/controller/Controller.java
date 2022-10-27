/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import model.Map;
import model.User;
import org.xml.sax.SAXException;

/**
 *
 * @author bbbbb
 */
public class Controller {
    private User user = new User();
    private State currentState;
    
    protected final InitialState initialState = new InitialState();
    protected final MapLoadedState mapLoadedState = new MapLoadedState();
    protected final CourierChosenState courierChosenState = new CourierChosenState();
    protected final DPEnteredState dpEnteredState = new DPEnteredState();
    protected final DPRestoredState dpRestoredState = new DPRestoredState();
    protected final DPRemovedState dpRemovedState = new DPRemovedState();
    protected final TourCalculatedState tourCalculatedState = new TourCalculatedState();
    protected final PlanGeneratedState planGeneratedState = new PlanGeneratedState();
    protected final DPSavedState dpSavedState = new DPSavedState();   

    public Controller() {
        this.currentState = initialState;
    }
    
    public User getUser() {
        return user;
    }

    protected void setCurrentState(State currentState) {
        this.currentState = currentState;
    }
    
    public Map loadMapFromXML(String XMLPath) throws ParserConfigurationException, IOException, SAXException {
        return this.currentState.loadMapFromXML(this, XMLPath);
    }
    
}
