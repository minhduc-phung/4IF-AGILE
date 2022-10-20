/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.pldagile.model;

/**
 *
 * @author nmngo
 */
public class Intersection {
    private Long id;
    private Double latitude;
    private Double longitude;

    public Intersection(Long id, Double latitude, Double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Intersection{" + "id=" + id + ", latitude=" + latitude + ", longitude=" + longitude + '}';
    }
    
}
