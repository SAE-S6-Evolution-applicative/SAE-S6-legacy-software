/*
 * Bill.java                                  19 mai. 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.bill;


import jakarta.persistence.*;
import sae.semestre.six.appointment.doctor.Doctor;
import sae.semestre.six.appointment.patient.Patient;
import sae.semestre.six.appointment.patient.history.PatientHistory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "bills")
public class Bill {

    private static final double REDUCTION_THRESHOLD = 500.0;
    private static final double REDUCTION_PERCENTAGE = 0.9;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bill_number", unique = true)
    private String billNumber;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @Column(name = "bill_date")
    private LocalDateTime billDate = LocalDateTime.now();

    @Column(name = "total_amount")
    private Double totalAmount = 0.0;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<BillDetail> billDetails = new HashSet<>();

    @Column(name = "created_date")
    private LocalDate createdDate = LocalDate.now();

    @Column(name = "last_modified")
    private LocalDate lastModified = LocalDate.now();

    @ManyToOne
    private PatientHistory patientHistory;

    @Column(name = "total_brut")
    private Double totalBrut = 0.0;

    @Transient
    private BillCopy copy;

    /**
     * Compute the reduction base of the totalBrut
     *
     * @param totalBrut the sum of all billDetail
     * @return total amount with reduction applied
     */
    public static double computeTotalAmountWithReduction(double totalBrut) {
        double totalAmount = totalBrut;
        if (totalBrut > REDUCTION_THRESHOLD) {
            totalAmount = totalBrut * REDUCTION_PERCENTAGE;
        }
        return totalAmount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public LocalDateTime getBillDate() {
        return billDate;
    }

    public void setBillDate(LocalDateTime billDate) {
        this.billDate = billDate;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        this.lastModified = LocalDate.now();
    }

    public Set<BillDetail> getBillDetails() {
        return billDetails;
    }

    /**
     * Adds a BillDetail to this bill.
     * Updates the billDetails set, sets the Bill reference in BillDetail,
     * updates the lastModified date, increments the totalBrut,
     * and recalculates the totalAmount with any applicable reduction.
     *
     * @param billDetail the BillDetail to add
     * @return this Bill instance
     */
    public Bill addBillDetail(BillDetail billDetail) {
        this.billDetails.add(billDetail);
        billDetail.setBill(this);
        this.lastModified = LocalDate.now();
        this.totalBrut += billDetail.getLineTotal();
        this.totalAmount = computeTotalAmountWithReduction(totalBrut);
        return this;
    }

    /**
     * Status of a Bill
     */
    public enum Status {
        PENDING,
        PAID
    }

    record BillCopy(
            String billNumber,
            Double totalAmount,
            Set<BillDetail> billDetails,
            Double totalBrut
    ) {
        public BillCopy(Bill bill) {
            this(bill.getBillNumber(), bill.getTotalAmount(), bill.getBillDetails(), bill.getTotalBrut());
        }

        public boolean equals(Bill bill) {
            return Objects.equals(this.billNumber, bill.getBillNumber()) &&
                    Objects.equals(this.totalAmount, bill.getTotalAmount()) &&
                    Objects.equals(this.billDetails, bill.getBillDetails()) &&
                    Objects.equals(this.totalBrut, bill.getTotalBrut());
        }
    }

    private Double getTotalBrut() {
        return totalBrut;
    }

    /**
     * When the entity is loaded, we create a copy of the entity,
     * So we can check if its have been change.
     */
    @PostLoad
    void copyPostLoad() {
        this.copy = new BillCopy(this);
    }

    /**
     * Ensures that the bill cannot be altered in db.
     * <br>
     * Check if copy is equal to the current instance. If doesn't throw an exception because the entity
     * can't be modified
     */
    @PreUpdate
    void checkUnalterability() {
        if (this.copy != null && !this.copy.equals(this)) {
            throw new BillModifiedException("Bill has been modified, your are not allowed to do that");
        }
    }
}