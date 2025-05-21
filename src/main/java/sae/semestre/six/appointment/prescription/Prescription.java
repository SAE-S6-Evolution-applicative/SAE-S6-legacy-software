/*
 * Prescription.java                                  19 mai. 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.prescription;

import jakarta.persistence.*;
import sae.semestre.six.appointment.patient.Patient;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "prescriptions")
public class Prescription {

    public static final double TVA_PERCENTAGE = 1.2;

    /**
     * Creates a new prescription with the specified parameters.
     *
     * @param previousPrescriptionNumber the previous prescription number used to generate the new prescription number
     * @param patient the patient associated with the prescription
     * @param medicines the list of medicines prescribed
     * @param notes additional notes regarding the prescription
     */
    public Prescription(int previousPrescriptionNumber, Patient patient, List<Medicine> medicines, String notes) {
        this.prescriptionNumber = "RX" + previousPrescriptionNumber;
        this.patient = patient;
        this.medicines = medicines;
        this.notes = notes;
        this.totalCost = calculateTotalCostTTC();
    }

    public Prescription() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "prescription_number")
    private String prescriptionNumber;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Column(name = "medicines")
    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Medicine> medicines;

    public void addMedicine(Medicine medicine) {
        this.medicines.add(medicine);
        // Remember to recalculate costs as the medicines list change
        this.totalCost = calculateTotalCostTTC();
        medicine.setPrescription(this);
    }

    public void removeMedicine(Medicine medicine) {
        this.medicines.remove(medicine);
        // Remember to recalculate costs as the medicines list change
        this.totalCost = calculateTotalCostTTC();
        medicine.setPrescription(null);
    }

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

    public List<Medicine> getMedicines() {
        return medicines;
    }

    public void setMedicines(List<Medicine> medicines) {
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

    /**
     * Calculates the total cost including tax (TTC) for all medicines in the prescription.
     * The total is computed as the sum of the prices of all medicines multiplied by the
     * TVA_PERCENTAGE constant.
     *
     * @return The total cost including tax (TTC) as a double.
     */
    private double calculateTotalCostTTC() {
        return medicines.stream().mapToDouble(Medicine::getUnitPrice).sum() * TVA_PERCENTAGE;
    }
} 