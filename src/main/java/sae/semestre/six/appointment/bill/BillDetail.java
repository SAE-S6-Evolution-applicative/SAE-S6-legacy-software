/*
 * BillDetail.java                                  19 mai. 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.bill;

import jakarta.persistence.*;
import sae.semestre.six.appointment.medicalact.MedicalAct;

import java.util.Objects;

@Entity
@Table(name = "bill_details")
public class BillDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bill_id", nullable = false)
    private Bill bill;

    @Column(nullable = false)
    private String nameMedicalAct;

    @Column(nullable = false)
    private Double priceMedicalAct;

    @Column(name = "quantity")
    private Integer quantity = 1;

    @Column(name = "line_total")
    private Double lineTotal = 0.0;

    @Transient
    private BillDetailCopy copy;

    public BillDetail() {
    }

    public BillDetail(MedicalAct medicalAct, Integer quantity) {
        this.quantity = quantity;
        this.nameMedicalAct = medicalAct.getName();
        this.priceMedicalAct = medicalAct.getPrice();
        calculateLineTotal();
    }

    public void calculateLineTotal() {
        this.lineTotal = this.quantity * this.priceMedicalAct;
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

    public String getNameMedicalAct() {
        return nameMedicalAct;
    }

    public Double getPriceMedicalAct() {
        return priceMedicalAct;
    }

    public void setMedicalAct(MedicalAct medicalAct) {
        if (!medicalAct.isActive()) {
            throw new IllegalArgumentException("MedicalAct is not active");
        }
        this.priceMedicalAct = medicalAct.getPrice();
        this.nameMedicalAct = medicalAct.getName();
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

    record BillDetailCopy(
            Double priceMedicalAct,
            Integer quantity,
            Double lineTotal
    ) {
        public BillDetailCopy(BillDetail billDetail) {
            this(billDetail.getPriceMedicalAct(), billDetail.getQuantity(), billDetail.getLineTotal());
        }

        public boolean equals(BillDetail billDetail) {
            return Objects.equals(this.priceMedicalAct, billDetail.getPriceMedicalAct()) &&
                    Objects.equals(this.quantity, billDetail.getQuantity()) &&
                    Objects.equals(this.lineTotal, billDetail.getLineTotal());
        }
    }

    /**
     * When the entity is loaded, we create a copy of the entity,
     * So we can check if its have been change.
     */
    @PostLoad
    void copyPostLoad() {
        this.copy = new BillDetailCopy(this);
    }

    /**
     * Ensures that the billDetail cannot be altered in db.
     * <br>
     * Check if the copy is equal to the current instance.
     * If doesn't throw an exception because the entity can't be modified
     */
    @PreUpdate
    void checkUnalterability() {
        if (this.copy != null && !this.copy.equals(this)) {
            throw new BillModifiedException("Bill detail has been modified, your are not allowed to do that");
        }
    }

    @PreRemove
    void preventDeletion() {
        throw new BillCannotBeDeletedException("A Bill cannot be deleted");
    }
}