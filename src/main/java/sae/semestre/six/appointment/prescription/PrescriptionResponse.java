/*
 * PrescriptionResponse.java                                 05 juin 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.prescription;

import java.util.List;

public class PrescriptionResponse {
    private Long id;
    private String prescriptionNumber;
    private String notes;
    private List<MedicineResponse> medicines;

    public PrescriptionResponse(Long id, String prescriptionNumber, String notes, List<MedicineResponse> medicines) {
        this.id = id;
        this.prescriptionNumber = prescriptionNumber;
        this.notes = notes;
        this.medicines = medicines;
    }

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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<MedicineResponse> getMedicines() {
        return medicines;
    }

    public void setMedicines(List<MedicineResponse> medicines) {
        this.medicines = medicines;
    }
}
