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

    private final HashMap<Integer, String> timeWindows = new HashMap<Integer, String>() {{
        put(8, "08:00 - 09:00");
        put(9, "09:00 - 10:00");
        put(10, "10:00 - 11:00");
        put(11, "11:00 - 12:00");
        put(12, "12:00 - 13:00");
        put(13, "13:00 - 14:00");
        put(14, "14:00 - 15:00");
        put(15, "15:00 - 16:00");
        put(16, "16:00 - 17:00");
        put(17, "17:00 - 18:00");
    }};

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
    public HashMap<Integer, String> getTimeWindows() {
        return timeWindows;
    }

}
