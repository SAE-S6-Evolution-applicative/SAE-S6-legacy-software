/*
 * BillDetail.java                                  19 mai. 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.bill;

import jakarta.persistence.*;
import sae.semestre.six.appointment.medicalact.MedicalAct;

@Entity
@Table(name = "bill_details")
public class BillDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bill_id", nullable = false)
    private Bill bill;

    @ManyToOne
    @JoinColumn(name = "medical_act")
    private MedicalAct medicalAct;

    @Column(name = "quantity")
    private Integer quantity = 1;

    @Column(name = "line_total")
    private Double lineTotal = 0.0;

    public BillDetail() {
    }

    public BillDetail(Bill bill, MedicalAct medicalAct, Integer quantity) {
        this.bill = bill;
        this.medicalAct = medicalAct;
        this.quantity = quantity;
        calculateLineTotal();
    }

    public BillDetail(MedicalAct medicalAct, Integer quantity) {
        this.medicalAct = medicalAct;
        this.quantity = quantity;
        calculateLineTotal();
    }

    public void calculateLineTotal() {
        this.lineTotal = this.quantity * this.medicalAct.getPrice();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public MedicalAct getMedicalAct() {
        return medicalAct;
    }

    public void setMedicalAct(MedicalAct medicalAct) {
        if (!medicalAct.isActive()) {
            throw new IllegalArgumentException("MedicalAct is not active");
        }
        this.medicalAct = medicalAct;
        calculateLineTotal();
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        calculateLineTotal();
    }

    public Double getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(Double lineTotal) {
        this.lineTotal = lineTotal;
    }
}