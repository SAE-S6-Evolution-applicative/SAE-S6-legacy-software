package sae.semestre.six.appointment.bill;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sae.semestre.six.appointment.doctor.Doctor;
import sae.semestre.six.appointment.doctor.DoctorRepository;
import sae.semestre.six.appointment.patient.Patient;
import sae.semestre.six.appointment.patient.PatientDao;
import sae.semestre.six.common.SuccessfullResponseModel;
import sae.semestre.six.appointment.patient.PatientRepository;
import sae.semestre.six.email.EmailService;

import java.io.FileWriter;
import java.util.*;

@RestController
@RequestMapping("/bills")
@Tag(name = "Billing", description = "Billing management API")
public class BillingController {

    private Map<String, Double> priceList = new HashMap<>();
    private double totalRevenue = 0.0;
    private List<String> pendingBills = new ArrayList<>();
    private BillService billService;

    @Autowired
    public BillController(BillRepository billRepository, PatientDao patientDao, DoctorDao doctorDao, BillService billService) {
        this();
        this.billRepository = billRepository;
        this.patientDao = patientDao;
        this.doctorDao = doctorDao;
        this.billService = billService;
    }

    
    private PatientRepository patientRepository;
    
    private DoctorRepository doctorRepository;
    
    private BillController() {
        priceList.put("CONSULTATION", 50.0);
        priceList.put("XRAY", 150.0);
        priceList.put("CHIRURGIE", 1000.0);
    }

    private BillRepository billRepository;
    private final EmailService emailService = EmailService.getInstance();

    @Operation(summary = "Process a bill", description = "Creates and processes a new bill for a patient")
    @ApiResponse(responseCode = "200", description = "Bill processed successfully")
    @ApiResponse(responseCode = "400", description = "Invalid data")
    @PostMapping
    public SuccessfullResponseModel processBill(
            @Parameter(description = "Patient ID") @RequestParam String patientId,
            @Parameter(description = "Doctor ID") @RequestParam String doctorId,
            @Parameter(description = "List of treatments") @RequestParam String[] treatments) {
            Patient patient = patientRepository.findById(Long.parseLong(patientId)).orElseThrow(
                    () -> new RuntimeException("Patient not found")
            );
            Doctor doctor = doctorRepository.findById(Long.parseLong(doctorId)).orElseThrow(
                    () -> new RuntimeException("Doctor not found")
            );
            
            Hibernate.initialize(doctor.getAppointments());

        Bill bill = new Bill();
        bill.setBillNumber("BILL" + System.currentTimeMillis());
        bill.setPatient(patient);
        bill.setDoctor(doctor);

        Hibernate.initialize(bill.getBillDetails());

        double total = 0.0;
        Set<BillDetail> details = new HashSet<>();

        for (String treatment : treatments) {
            double price = priceList.get(treatment);
            total += price;

            BillDetail detail = new BillDetail();
            detail.setBill(bill);
            detail.setTreatmentName(treatment);
            detail.setUnitPrice(price);
            details.add(detail);

            Hibernate.initialize(detail);
        }

        if (total > 500) {
            total = total * 0.9;
        }

        bill.setTotalAmount(total);
        bill.setBillDetails(details);

        try (FileWriter fw = new FileWriter("C:\\hospital\\billing.txt", true)) {
            fw.write(bill.getBillNumber() + ": $" + total + "\n");
        }

        totalRevenue += total;
        billRepository.save(bill);

        emailService.sendEmail(
                "admin@hospital.com",
                "New Bill Generated",
                "Bill Number: " + bill.getBillNumber() + "\nTotal: $" + total
        );

        return new SuccessfullResponseModel("Bill processed successfully", true);
    }
    
    @Operation(summary = "Update treatment price", description = "Modifies the price of a treatment type")
    @ApiResponse(responseCode = "200", description = "Price updated successfully")
    @PutMapping("/price")
    public SuccessfullResponseModel updatePrice(
            @Parameter(description = "Treatment type") @RequestParam String treatment,
            @Parameter(description = "New price") @RequestParam double price) throws Exception {
        priceList.put(treatment, price);
        recalculateAllPendingBills();
        return new SuccessfullResponseModel("Price updated", true);
    }

    private void recalculateAllPendingBills() throws Exception {
        for (String billId : pendingBills) {
            processBill(billId, "RECALC", new String[]{"CONSULTATION"});
        }
    }
    
    @Operation(summary = "Get price list", description = "Retrieves all treatment prices")
    @ApiResponse(responseCode = "200", description = "Price list")
    @GetMapping("/prices")
    public PricesResponse getPrices() {
        return new PricesResponse(priceList);
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

    public record PricesResponse(Map<String, Double> prices) {
    }

    public record PendingBillResponse(List<String> pendingBills) {
    }
} 