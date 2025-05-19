/*
 * BillController.java                                  19 mai. 2025
 * IUT de Rodez, pas de droit d'auteur
 */

package sae.semestre.six.appointment.bill;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sae.semestre.six.appointment.doctor.Doctor;
import sae.semestre.six.appointment.doctor.DoctorService;
import sae.semestre.six.appointment.medicalact.MedicalAct;
import sae.semestre.six.appointment.medicalact.MedicalActService;
import sae.semestre.six.appointment.patient.Patient;
import sae.semestre.six.appointment.patient.PatientService;
import sae.semestre.six.common.SuccessfullResponseModel;
import sae.semestre.six.email.EmailService;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/bills")
@Tag(name = "Billing", description = "Billing management API")
public class BillController {

    private static final Logger logger = LoggerFactory.getLogger(BillController.class);

    private BillService billService;

    private MedicalActService medicalActService;

    private PatientService patientService;

    private DoctorService doctorService;

    private EmailService emailService;

    @Autowired
    public BillController(
            BillService billService,
            MedicalActService medicalActService,
            PatientService patientService,
            DoctorService doctorService,
            EmailService emailService
    ) {
        this.billService = billService;
        this.medicalActService = medicalActService;
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.emailService = emailService;
    }

    @Operation(summary = "Process a bill", description = "Creates and processes a new bill for a patient")
    @ApiResponse(responseCode = "200", description = "Bill processed successfully")
    @ApiResponse(responseCode = "400", description = "Invalid data")
    @PostMapping
    public SuccessfullResponseModel processBill(
            @Parameter(description = "Patient ID") @RequestParam String patientId,
            @Parameter(description = "Doctor ID") @RequestParam String doctorId,
            @Parameter(description = "List of medical acts id") @RequestParam Long[] medicalActId
    ) throws Exception {

        Patient patient = patientService.getPatient(Long.parseLong(patientId));
        Doctor doctor = doctorService.getDoctor(Long.parseLong(doctorId));

        List<MedicalAct> medicalActs = medicalActService.findByIds(medicalActId);

        Bill bill = billService.processBill(patient, doctor, medicalActs);

        logger.info("{}: ${}", bill.getBillNumber(), bill.getTotalAmount());

        emailService.sendEmail(
                "admin@hospital.com",
                "New Bill Generated",
                "Bill Number: " + bill.getBillNumber() + "\nTotal: $" + bill.getTotalAmount()
        );

        return new SuccessfullResponseModel("Bill processed successfully", true);
    }

    @Operation(summary = "Calculate insurance coverage", description = "Calculates insurance coverage for a given amount")
    @ApiResponse(responseCode = "200", description = "Coverage calculated")
    @GetMapping("/insurance-coverage")
    public InsuranceCoverageResponse calculateInsurance(
            @Parameter(description = "Amount to cover") @RequestParam double amount
    ) {
        return new InsuranceCoverageResponse(amount);
    }

    @Operation(summary = "Get total revenue", description = "Retrieves the total system revenue")
    @ApiResponse(responseCode = "200", description = "Total revenue")
    @GetMapping("/revenue")
    public RevenueResponse getTotalRevenue() {
        return new RevenueResponse(billService.getTotalRevenue());
    }

    @Operation(summary = "Get pending bills", description = "Retrieves the list of pending bills")
    @ApiResponse(responseCode = "200", description = "List of pending bills")
    @GetMapping("/pending")
    public PendingBillResponse getPendingBills() {
        return new PendingBillResponse(
                billService.findPendingBills().stream()
                        .map(Bill::getId)
                        .map(Objects::toString)
                        .toList()
        );
    }

    public record RevenueResponse(double totalRevenue) {
    }

    public record InsuranceCoverageResponse(double amount) {
    }

    public record PendingBillResponse(List<String> pendingBills) {
    }
} 