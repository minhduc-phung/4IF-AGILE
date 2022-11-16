/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * This class defines the couriers and save every changes on the program relating to each of them
 * (the delivery points chosen for them, the tour for them to take)
 */
public class User {

    /**
     * The list of couriers
     */
    private HashMap<Long, Courier> listCourier = new HashMap<>();
    /**
     * The list of the default time windows
     * The LinkedHashMap is used to keep the order of insertion
     */
    private final LinkedHashMap<String, Integer> timeWindows = new LinkedHashMap<String, Integer>() {
        {
            put("08:00 - 09:00", 8);
            put("09:00 - 10:00", 9);
            put("10:00 - 11:00", 10);
            put("11:00 - 12:00", 11);
            put("12:00 - 13:00", 12);
            put("13:00 - 14:00", 13);
            put("14:00 - 15:00", 14);
            put("15:00 - 16:00", 15);
            put("16:00 - 17:00", 16);
            put("17:00 - 18:00", 17);
        }
    };

    /**
     * Create a user with 5 example couriers with unrealistic names
     */
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

    public Courier getCourierById(Long idCourier) {
        return listCourier.get(idCourier);
    }

    public Courier getCourierByName(String nameCourier) {
        Courier chosenCourier = null;
        for (Courier c : this.listCourier.values()) {
            if (c.getName().equals(nameCourier)) {
                chosenCourier = c;
            }
        }
        return chosenCourier;
    }

    public LinkedHashMap<String, Integer> getTimeWindows() {
        return timeWindows;
    }

    public void addWarehouse(Map map, Long idCourier) {
        DeliveryPoint dpWarehouse = new DeliveryPoint(map.getWarehouse().getId(), map.getWarehouse().getLatitude(), map.getWarehouse().getLongitude());
        Courier courier = this.listCourier.get(idCourier);
        dpWarehouse.chooseCourier(courier);
        courier.addDeliveryPoint(dpWarehouse);
        courier.addPositionIntersection(dpWarehouse.getId());
        HashMap<Long, Double> nestedMap = new HashMap<>();
        nestedMap.put(dpWarehouse.getId(), 0.0);
        courier.getShortestPathBetweenDPs().put(dpWarehouse.getId(), nestedMap);
        Tour tour = new Tour();
        tour.addTourRoute(dpWarehouse.getId(), new ArrayList<>());
        courier.getListSegmentBetweenDPs().put(dpWarehouse.getId(), tour);
    }
}
