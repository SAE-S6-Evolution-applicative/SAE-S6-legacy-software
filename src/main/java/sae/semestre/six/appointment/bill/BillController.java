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
import sae.semestre.six.appointment.patient.PatientRepository;
import sae.semestre.six.email.EmailService;

import java.io.FileWriter;
import java.util.*;

@RestController
@RequestMapping("/bills")
@Tag(name = "Billing", description = "Billing management API")
public class BillingController {

    private static volatile BillController instance;
    private Map<String, Double> priceList = new HashMap<>();
    private double totalRevenue = 0.0;
    private List<String> pendingBills = new ArrayList<>();

    @Autowired
    public BillController(BillRepository billRepository, PatientDao patientDao, DoctorDao doctorDao) {
        this();
        this.billRepository = billRepository;
        this.patientDao = patientDao;
        this.doctorDao = doctorDao;
    }

    
    private PatientRepository patientRepository;
    
    private DoctorRepository doctorRepository;
    
    private BillRepository billRepository;
    private final EmailService emailService = EmailService.getInstance();

    private BillController() {
        priceList.put("CONSULTATION", 50.0);
        priceList.put("XRAY", 150.0);
        priceList.put("CHIRURGIE", 1000.0);
    }

    public static BillController getInstance() {
        if (instance == null) {
            synchronized (BillController.class) {
                if (instance == null) {
                    instance = new BillController();
                }
            }
        }
        return instance;
    }
    
    @Operation(summary = "Process a bill", description = "Creates and processes a new bill for a patient")
    @ApiResponse(responseCode = "200", description = "Bill processed successfully")
    @ApiResponse(responseCode = "400", description = "Invalid data")
    @PostMapping
    public String processBill(
            @Parameter(description = "Patient ID") @RequestParam String patientId,
            @Parameter(description = "Doctor ID") @RequestParam String doctorId,
            @Parameter(description = "List of treatments") @RequestParam String[] treatments) {
        try {
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

            return "Bill processed successfully";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    @Operation(summary = "Update treatment price", description = "Modifies the price of a treatment type")
    @ApiResponse(responseCode = "200", description = "Price updated successfully")
    @PutMapping("/price")
    public String updatePrice(
            @Parameter(description = "Treatment type") @RequestParam String treatment,
            @Parameter(description = "New price") @RequestParam double price) {
        priceList.put(treatment, price);
        recalculateAllPendingBills();
        return "Price updated";
    }

    private void recalculateAllPendingBills() {
        for (String billId : pendingBills) {
            processBill(billId, "RECALC", new String[]{"CONSULTATION"});
        }
    }
    
    @Operation(summary = "Get price list", description = "Retrieves all treatment prices")
    @ApiResponse(responseCode = "200", description = "Price list")
    @GetMapping("/prices")
    public Map<String, Double> getPrices() {
        return priceList;
    }
    
    @Operation(summary = "Calculate insurance coverage", description = "Calculates insurance coverage for a given amount")
    @ApiResponse(responseCode = "200", description = "Coverage calculated")
    @GetMapping("/insurance-coverage")
    public String calculateInsurance(
            @Parameter(description = "Amount to cover") @RequestParam double amount) {
        double coverage = amount;
        return "Insurance coverage: $" + coverage;
    }
    
    @Operation(summary = "Get total revenue", description = "Retrieves the total system revenue")
    @ApiResponse(responseCode = "200", description = "Total revenue")
    @GetMapping("/revenue")
    public String getTotalRevenue() {
        return "Total Revenue: $" + totalRevenue;
    }
    
    @Operation(summary = "Get pending bills", description = "Retrieves the list of pending bills")
    @ApiResponse(responseCode = "200", description = "List of pending bills")
    @GetMapping("/pending")
    public List<String> getPendingBills() {
        return pendingBills;
    }
} 