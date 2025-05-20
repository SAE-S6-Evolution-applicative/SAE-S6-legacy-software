/*
 * Prescription.java                                  19 mai. 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.prescription;

import jakarta.persistence.*;
import sae.semestre.six.appointment.Appointment;
import sae.semestre.six.appointment.patient.Patient;

import java.time.LocalDateTime;

@Entity
@Table(name = "prescriptions")
public class Prescription {

    public Prescription(int previousPrescriptionNumber, Patient patient, String[] medicines, String notes) {
        this.prescriptionNumber = "RX" + previousPrescriptionNumber;
        this.patient = patient;
        this.medicines = String.join(",", medicines);
        this.notes = notes;
    }

    Prescription prescription = new Prescription();
            prescription.setPrescriptionNumber(prescriptionId);
            prescription.setPatient(patient);

            prescription.setMedicines(String.join(",", medicines));
            prescription.setNotes(notes);

    double cost = calculateCost(prescriptionId);
            prescription.setTotalCost(cost);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "prescription_number")
    private String prescriptionNumber;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Column(name = "medicines")
    private String medicines;

    @Column(name = "notes")
    private String notes;

    @Column(name = "total_cost")
    private Double totalCost;

    @Column(name = "is_billed")
    private Boolean isBilled = false;

    @Column(name = "inventory_updated")
    private Boolean inventoryUpdated = false;


    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "last_modified")
    private LocalDateTime lastModified = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrescriptionNumber() {
        return prescriptionNumber;
    }

    public void setPrescriptionNumber(String prescriptionNumber) {
        this.prescriptionNumber = prescriptionNumber;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getMedicines() {
        return medicines;
    }

    public void setMedicines(String medicines) {
        this.medicines = medicines;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public Boolean getBilled() {
        return isBilled;
    }

    public void setBilled(Boolean billed) {
        isBilled = billed;
        lastModified = LocalDateTime.now();
    }

    public Boolean getInventoryUpdated() {
        return inventoryUpdated;
    }

    public void setInventoryUpdated(Boolean inventoryUpdated) {
        this.inventoryUpdated = inventoryUpdated;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    @PreUpdate
    public void preUpdate() {
        lastModified = LocalDateTime.now();
    }

    /**
     * Extracts the numeric part from a prescription number string.
     *
     * @return The numeric part as an integer, or 0 if no numeric part is found
     */
    public int extractNumericPartFromPrescriptionNumber() {
        if (prescriptionNumber == null || prescriptionNumber.isEmpty()) {
            return 0;
        }

        String numericPart = prescriptionNumber.replaceAll("[^0-9]", "");

        // Convert to integer, default to 0 if no digits were found
        try {
            return Integer.parseInt(numericPart);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
} 