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
import sae.semestre.six.appointment.doctor.DoctorRepository;
import sae.semestre.six.appointment.medicalact.MedicalAct;
import sae.semestre.six.appointment.medicalact.MedicalActService;
import sae.semestre.six.appointment.patient.Patient;
import sae.semestre.six.appointment.patient.PatientRepository;
import sae.semestre.six.common.SuccessfullResponseModel;
import sae.semestre.six.email.EmailService;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/bills")
@Tag(name = "Billing", description = "Billing management API")
public class BillingController {

    private static final Logger logger = LoggerFactory.getLogger(BillController.class);

    private BillService billService;

    private MedicalActService medicalActService;

    private PatientRepository patientRepository;

    private DoctorRepository doctorRepository;

    private final EmailService emailService = EmailService.getInstance();

    @Autowired
    public BillController(
            DoctorRepository doctorRepository, PatientRepository patientRepository, BillService billService, MedicalActService medicalActService
    ) {
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.billService = billService;
        this.medicalActService = medicalActService;
    }

    private BillController() {
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

            Patient patient = patientRepository.findById(Long.parseLong(patientId)).orElseThrow(
                    () -> new RuntimeException("Patient not found")
            );
            Doctor doctor = doctorRepository.findById(Long.parseLong(doctorId)).orElseThrow(
                    () -> new RuntimeException("Doctor not found")
            );
            
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
        double coverage = amount;
        return new InsuranceCoverageResponse(coverage);
    }
    
    @Operation(summary = "Get total revenue", description = "Retrieves the total system revenue")
    @ApiResponse(responseCode = "200", description = "Total revenue")
    @GetMapping("/revenue")
    public RevenueResponse getTotalRevenue() {
        Double totalRevenue = billService.getTotalRevenue();
        if (totalRevenue == null) {
            totalRevenue = 0.0;
        }
        return new RevenueResponse(totalRevenue);
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