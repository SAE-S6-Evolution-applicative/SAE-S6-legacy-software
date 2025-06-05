/*
 * PrescriptionController.java                                  19 mai. 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.prescription;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sae.semestre.six.exception.EntityNotFoundException;
import sae.semestre.six.exception.GlobalExceptionHandler;

import java.util.List;

@RestController
@RequestMapping("/prescriptions")
@Tag(name = "Prescriptions", description = "Prescription management API")
public class PrescriptionController {
    
    private final PrescriptionService prescriptionService;

    @Autowired
    public PrescriptionController(final PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @Operation(summary = "Add a prescription", description = "Creates a new prescription for a patient")
    @ApiResponse(responseCode = "201", description = "Prescription created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid data")
    @PostMapping
    public ResponseEntity<Void> addPrescription(@RequestBody PrescriptionRequest request) {
        prescriptionService.addPrescription(request.patientId(), request.medicineIds(), request.notes());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Get patient prescriptions", description = "Retrieves all prescriptions for a patient")
    @ApiResponse(responseCode = "200", description = "List of prescriptions")
    @GetMapping("/{patientId}")
    public List<PrescriptionResponse> getPatientPrescriptions(
            @Parameter(description = "Patient ID") @PathVariable Long patientId) {
        List<Prescription> prescriptions = prescriptionService.findAllPrescriptionsByPatientId(patientId);
        return prescriptions.stream().map(prescription -> new PrescriptionResponse(
                prescription.getId(),
                prescription.getPrescriptionNumber(),
                prescription.getNotes(),
                prescription.getMedicines().stream().map(
                        medicine -> new MedicineResponse(
                                medicine.getId(),
                                medicine.getName(),
                                medicine.getUnitPrice()
                        )
                ).toList()
        )).toList();
    }

    @Operation(summary = "Calculate prescription cost", description = "Calculates the total cost of a prescription")
    @ApiResponse(responseCode = "200", description = "Cost calculated")
    @GetMapping("/{prescriptionId}/cost")
    public double calculateCost(
            @Parameter(description = "Prescription ID") @PathVariable Long prescriptionId) {
        return prescriptionService.getTotalCost(prescriptionId);
    }

    /**
     * Represents a request containing the necessary information to create a prescription.
     *
     * This record is used within the prescription management system to encapsulate the
     * details required to create a new prescription, including the patient ID, the list
     * of medicine IDs to be prescribed, and any additional notes.
     *
     * Fields:
     * - patientId: The unique identifier of the patient for whom the prescription is created.
     * - medicineIds: A list of unique identifiers for the medicines being prescribed.
     * - notes: Any additional information or instructions associated with the prescription.
     */
    record PrescriptionRequest(Long patientId, List<Long> medicineIds, String notes) {}
}