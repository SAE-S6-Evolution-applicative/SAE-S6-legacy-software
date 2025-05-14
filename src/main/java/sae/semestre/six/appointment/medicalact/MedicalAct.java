/*
 * Treatment.java                                  14 mai 2025
 * IUT de Rodez, pas de droit d'auteur
 */

package sae.semestre.six.appointment.medicalact;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * This entity represents in medical action with is cost.
 */
@Entity
public class MedicalAct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double price;

    /**
     * To keep a history of past price, we need to keep all MedicalAct in the DB. So an updated MedicalAct cannot be chosen for a bill.
     */
    private boolean active;

    public MedicalAct(String name, double price) {
        this.name = name;
        this.price = price;
        this.active = true;
    }

    public MedicalAct() {
    }

    public MedicalAct updatePrice(double price) {
        MedicalAct copy = new MedicalAct(name, price);
        this.active = false;
        return copy;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}