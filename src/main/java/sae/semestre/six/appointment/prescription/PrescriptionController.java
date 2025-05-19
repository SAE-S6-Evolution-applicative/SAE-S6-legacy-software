/*
 * PrescriptionController.java                                  19 mai. 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.prescription;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sae.semestre.six.appointment.bill.BillService;
import sae.semestre.six.appointment.patient.Patient;
import sae.semestre.six.appointment.patient.PatientRepository;

import java.io.FileWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/prescriptions")
@Tag(name = "Prescriptions", description = "Prescription management API")
public class PrescriptionController {

    private static final Map<String, List<String>> patientPrescriptions = new HashMap<>();
    private static final Map<String, Integer> medicineInventory = new HashMap<>();

    private static final Map<String, Double> medicinePrices = new HashMap<String, Double>() {{
        put("PARACETAMOL", 5.0);
        put("ANTIBIOTICS", 25.0);
        put("VITAMINS", 15.0);
    }};
    private static final String AUDIT_FILE = "C:\\hospital\\prescriptions.log";
    private static int prescriptionCounter = 0;

    private BillService billService;

    private PatientRepository patientRepository;

    private PrescriptionRepository prescriptionRepository;

    @Autowired
    public PrescriptionController(
            BillService billService,
            PatientRepository patientRepository,
            PrescriptionRepository prescriptionRepository
    ) {
        this.billService = billService;
        this.patientRepository = patientRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    @Operation(summary = "Add a prescription", description = "Creates a new prescription for a patient")
    @ApiResponse(responseCode = "200", description = "Prescription created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid data")
    @PostMapping
    public String addPrescription(
            @Parameter(description = "Patient ID") @RequestParam String patientId,
            @Parameter(description = "List of medicines") @RequestParam String[] medicines,
            @Parameter(description = "Additional notes") @RequestParam String notes) {
        try {
            prescriptionCounter++;
            String prescriptionId = "RX" + prescriptionCounter;

            Prescription prescription = new Prescription();
            prescription.setPrescriptionNumber(prescriptionId);

            Patient patient = patientRepository.findById(Long.parseLong(patientId)).orElseThrow(
                    () -> new RuntimeException("Patient not found")
            );
            prescription.setPatient(patient);

            prescription.setMedicines(String.join(",", medicines));
            prescription.setNotes(notes);

            double cost = calculateCost(prescriptionId);
            prescription.setTotalCost(cost);


            prescriptionRepository.save(prescription);


            new FileWriter(AUDIT_FILE, true)
                    .append(LocalDate.now() + " - " + prescriptionId + "\n")
                    .close();


            List<String> currentPrescriptions = patientPrescriptions.getOrDefault(patientId, new ArrayList<>());
            currentPrescriptions.add(prescriptionId);
            patientPrescriptions.put(patientId, currentPrescriptions);


            billService.processBill(
                    patientId,
                    "SYSTEM",
                    new String[]{"PRESCRIPTION_" + prescriptionId}
            );


            for (String medicine : medicines) {
                int current = medicineInventory.getOrDefault(medicine, 0);
                medicineInventory.put(medicine, current - 1);
            }

            return "Prescription " + prescriptionId + " created and billed";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed: " + e;
        }
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