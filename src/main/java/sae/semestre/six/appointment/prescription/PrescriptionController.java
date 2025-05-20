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
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/prescriptions")
@Tag(name = "Prescriptions", description = "Prescription management API")
public class PrescriptionController {


    private static final Logger logger = LoggerFactory.getLogger(PrescriptionController.class);

    private static final Map<String, List<String>> patientPrescriptions = new HashMap<>();
    private static final Map<String, Integer> medicineInventory = new HashMap<>();
    
    private final PrescriptionService prescriptionService;

    @Autowired
    public PrescriptionController(final PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @Operation(summary = "Add a prescription", description = "Creates a new prescription for a patient")
    @ApiResponse(responseCode = "200", description = "Prescription created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid data")
    @PostMapping
    public void addPrescription( //TODO change to ResponseModel
            @Parameter(description = "Patient ID") @RequestParam Long patientId,
            @Parameter(description = "List of medicines") @RequestParam String[] medicines,
            @Parameter(description = "Additional notes") @RequestParam String notes) {

        prescriptionService.addPrescription(patientId, medicines, notes);
    }

    @Operation(summary = "Get patient prescriptions", description = "Retrieves all prescriptions for a patient")
    @ApiResponse(responseCode = "200", description = "List of prescriptions")
    @GetMapping("/patient/{patientId}")
    public List<String> getPatientPrescriptions(
            @Parameter(description = "Patient ID") @PathVariable String patientId) {
        return patientPrescriptions.getOrDefault(patientId, new ArrayList<>());
    }

    @Operation(summary = "Get medicine inventory", description = "Retrieves the current medicine inventory status")
    @ApiResponse(responseCode = "200", description = "Inventory status")
    @GetMapping("/medicines/inventory")
    public Map<String, Integer> getInventory() {
        return medicineInventory;
    }

    @Operation(summary = "Refill medicine", description = "Adds quantities to the medicine inventory")
    @ApiResponse(responseCode = "200", description = "Refill completed")
    @PatchMapping("/medicines/refill")
    public String refillMedicine(
            @Parameter(description = "Medicine name") @RequestParam String medicine,
            @Parameter(description = "Quantity to add") @RequestParam int quantity) {
        medicineInventory.put(medicine,
                medicineInventory.getOrDefault(medicine, 0) + quantity);
        return "Refilled " + medicine;
    }

    @Operation(summary = "Calculate prescription cost", description = "Calculates the total cost of a prescription")
    @ApiResponse(responseCode = "200", description = "Cost calculated")
    @GetMapping("/{prescriptionId}/cost")
    public double calculateCost(
            @Parameter(description = "Prescription ID") @PathVariable String prescriptionId) {
        return medicinePrices.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum() * 1.2;
    }

    @Operation(summary = "Clear all data", description = "Clears all prescription data")
    @ApiResponse(responseCode = "200", description = "Data cleared")
    @DeleteMapping
    public void clearAllData() {
        patientPrescriptions.clear();
        medicineInventory.clear();
        prescriptionCounter = 0;
    }
} 