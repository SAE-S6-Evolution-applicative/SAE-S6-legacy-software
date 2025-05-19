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

    public Bill addBillDetail(BillDetail billDetail) {
        this.billDetails.add(billDetail);
        billDetail.setBill(this);
        this.lastModified = LocalDate.now();
        this.totalBrut += billDetail.getLineTotal();
        this.totalAmount = computeTotalAmountWithReduction(totalBrut);
        return this;
    }

    /**
     * Recalculate the total amount of the bill.
     * @return the recalculated bill
     */
    public Bill recalculate() {
        if (getStatus() != Status.PENDING) {
            throw new IllegalArgumentException("Bill is not PENDING");
        }

        double totalBrut = billDetails.stream()
                .map(BillDetail::getLineTotal)
                .reduce(0.0, Double::sum);
        this.totalAmount = computeTotalAmountWithReduction(totalBrut);
        return this;
    }

    public enum Status {
        PENDING,
        PAID
    }
}