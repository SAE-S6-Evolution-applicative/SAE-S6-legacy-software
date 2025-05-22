/*
 * Medicine.java                                 20 mai 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.prescription;

import jakarta.persistence.*;
import sae.semestre.six.stock.Inventory;

@Entity
@Table(name = "medicine")
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String name;

    private Double unitPrice;

    @ManyToOne
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;

    @OneToOne(mappedBy = "medicine")
    private Inventory inventory;

    public Medicine() {}

    public Medicine(String name, double unitPrice) {
        this.name = name;
        this.unitPrice = unitPrice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Prescription getPrescription() {
        return prescription;
    }

    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
}
