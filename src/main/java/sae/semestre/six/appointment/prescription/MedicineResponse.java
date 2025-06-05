/*
 * MedicineResponse.java                                 05 juin 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.prescription;

public class MedicineResponse {
    private Long id;
    private String name;
    private double unitPrice;

    public MedicineResponse(Long id, String name, double unitPrice) {
        this.id = id;
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

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
}