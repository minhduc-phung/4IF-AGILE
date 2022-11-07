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
    private String mapSource;

    public User() {
        Courier c1 = new Courier(Long.parseLong("1"), "GIYRAUD Vincent");
        Courier c2 = new Courier(Long.parseLong("2"), "KLOEN Benjamin");
        Courier c3 = new Courier(Long.parseLong("3"), "PATTIT Philipe");
        Courier c4 = new Courier(Long.parseLong("4"), "BAURGIOOS Rafael");
        Courier c5 = new Courier(Long.parseLong("5"), "OGARWAL Alexandru");
        this.listCourier.put(c1.getId(), c1);
        this.listCourier.put(c2.getId(), c2);
        this.listCourier.put(c3.getId(), c3);
        this.listCourier.put(c4.getId(), c4);
        this.listCourier.put(c5.getId(), c5);
    }
    
    public HashMap<Long, Courier> getListCourier() {
        return this.listCourier;
    }

    public String[] getListCourierName() {
        String[] listCourierName = new String[this.listCourier.size()];
        int i = 0;
        for (Courier c : this.listCourier.values()) {
            listCourierName[i] = c.getName();
            i++;
        }
        return listCourierName;
    }
    public Courier getCourierById(Long idCourier){
        return listCourier.get(idCourier);
    }

    public Courier getCourierByName(String nameCourier){
        Courier chosenCourier = null;
        for (Courier c : this.listCourier.values()) {
            if(c.getName().equals(nameCourier)){
                chosenCourier = c;
            }
        }
        return chosenCourier;
    }

    public String getMapSource() {
        return mapSource;
    }

    public void setMapSource(String mapSource) {
        this.mapSource = mapSource;
    }
    
}
