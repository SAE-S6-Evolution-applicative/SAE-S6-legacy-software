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
    @ApiResponse(responseCode = "200", description = "Prescription created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid data")
    @PostMapping
    public ResponseEntity<Void> addPrescription(
            @Parameter(description = "Patient ID") @RequestBody Long patientId,
            @Parameter(description = "List of medicines") @RequestBody List<Long> medicineIds,
            @Parameter(description = "Additional notes") @RequestBody String notes) {

        prescriptionService.addPrescription(patientId, medicineIds, notes);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Get patient prescriptions", description = "Retrieves all prescriptions for a patient")
    @ApiResponse(responseCode = "200", description = "List of prescriptions")
    @GetMapping("/prescriptions/{patientId}")
    public List<Prescription> getPatientPrescriptions(
            @Parameter(description = "Patient ID") @PathVariable Long patientId) {
        return prescriptionService.findAllPrescriptionsByPatientId(patientId);
    }

    @Operation(summary = "Calculate prescription cost", description = "Calculates the total cost of a prescription")
    @ApiResponse(responseCode = "200", description = "Cost calculated")
    @GetMapping("/{prescriptionId}/cost")
    public double calculateCost(
            @Parameter(description = "Prescription ID") @PathVariable Long prescriptionId) {
        return prescriptionService.getTotalCost(prescriptionId);
    }
} 