/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.HashMap;

/**
 *
 * @author bbbbb
 */
public class User {
    private HashMap<Long, Courier> listCourier = new HashMap<>();
    
    public User() {
        Courier c1 = new Courier(Long.parseLong("1"), "Viet");
        Courier c2 = new Courier(Long.parseLong("2"), "Duc");
        Courier c3 = new Courier(Long.parseLong("3"), "Minh");
        Courier c4 = new Courier(Long.parseLong("4"), "Chloe");
        Courier c5 = new Courier(Long.parseLong("5"), "Yi");
        Courier c6 = new Courier(Long.parseLong("6"), "Aicha");
        this.listCourier.put(c1.getId(), c1);
        this.listCourier.put(c2.getId(), c2);
        this.listCourier.put(c3.getId(), c3);
        this.listCourier.put(c4.getId(), c4);
        this.listCourier.put(c5.getId(), c5);
        this.listCourier.put(c6.getId(), c6);
    }
    
    public HashMap<Long, Courier> getListCourier() {
        return this.listCourier;
    }
    
    public Courier getCourier(Long idCourier){
        Courier chosenCourier = listCourier.get(idCourier);
        return chosenCourier;
    }
}
