package sae.semestre.six.appointment.bill;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.WebContentGenerator;
import sae.semestre.six.FileHandler;
import sae.semestre.six.appointment.Appointment;
import sae.semestre.six.appointment.AppointmentRepository;
import sae.semestre.six.appointment.doctor.Doctor;
import sae.semestre.six.appointment.doctor.DoctorRepository;
import sae.semestre.six.appointment.medicalact.MedicalAct;
import sae.semestre.six.appointment.medicalact.MedicalActRepository;
import sae.semestre.six.appointment.medicalact.MedicalActService;
import sae.semestre.six.appointment.patient.Patient;
import sae.semestre.six.appointment.patient.PatientRepository;
import sae.semestre.six.email.EmailService;
import static org.mockito.Mockito.when;


import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.mail.host=localhost",
        "spring.mail.port=3025",
        "spring.mail.username=",
        "spring.mail.password="
})
class BillControllerIntegrationTest {
    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("test", "test"));

    @Autowired
    private MockMvc server;

    @MockitoSpyBean
    private BillRepository billRepository;

    @MockitoSpyBean
    private BillService billService;

    @MockitoSpyBean
    private PatientRepository patientRepository;

    @MockitoSpyBean
    private DoctorRepository doctorRepository;

    @MockitoSpyBean
    private MedicalActService medicalActService;

    @MockitoSpyBean
    private MedicalActRepository medicalActRepository;

    @MockitoSpyBean
    private BillDetailRepository billDetailRepository;

    @MockitoSpyBean
    private AppointmentRepository appointmentRepository;

    @MockitoSpyBean
    private EmailService emailService;

    @MockitoSpyBean
    private FileHandler fileHandler;

    @Value("${app.security.hash-folder-path}")
    private String hashFolder;
    @Autowired
    private WebContentGenerator webContentGenerator;

    @AfterEach
    void deleteBillFileHash() throws IOException {
        deleteFileRecursive(new File(hashFolder));
    }

    private static void deleteFileRecursive(File fileToDelete) throws IOException {
        if (fileToDelete.isDirectory()) {
            File[] childFilesToDelete = fileToDelete.listFiles();
            if (childFilesToDelete != null) {
                Arrays.stream(childFilesToDelete)
                        .forEach(fileToDelete1 -> {
                            try {
                                deleteFileRecursive(fileToDelete1);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
            }
        }

        fileToDelete.delete();
    }

    @Test
    void testProcessBill() throws Exception {
        // Given a patient, doctor, appointment and a medical act
        Patient patient = new Patient();
        patient.setPatientNumber("PAT001");
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patientRepository.save(patient);
        patient = patientRepository.findByPatientNumber(patient.getPatientNumber());

        Doctor doctor = new Doctor();
        doctor.setDoctorNumber("DOCTOR001");
        doctor.setFirstName("John");
        doctor.setLastName("Doe");
        doctorRepository.save(doctor);

        Appointment appointment = new Appointment();
        appointment.setAppointmentNumber("APPOINTMENT001");
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(LocalDateTime.now());
        appointment = appointmentRepository.save(appointment);

        doctor = doctorRepository.findByDoctorNumber(doctor.getDoctorNumber());
        doctor.addAppointment(appointment);
        doctorRepository.save(doctor);

        MedicalAct consultation = medicalActRepository.save(new MedicalAct("CONSULTATION", 10.0));

        // When the process bill endpoint is called
        server.perform(post("/bills")
                        .param("patientId", patient.getId().toString())
                        .param("doctorId", doctor.getId().toString())
                        .param("medicalActId", consultation.getId().toString()))
                // Then...
                .andExpect(status().isOk());

        // Then the bill should be created and associated with the patient and doctor
        Bill billCreated = billRepository.findBillsByDoctor_Id(doctor.getId()).get(0);
        assertEquals(doctor, billCreated.getDoctor());

        BillDetail billDetail = billCreated.getBillDetails().stream().findAny().get();
        assertEquals(10, billDetail.getLineTotal());
        assertEquals(consultation.getPrice(), billDetail.getPriceMedicalAct());
        assertEquals(consultation.getName(), billDetail.getNameMedicalAct());
    }

    @Test
    void testProcessBill_ButOneDontMatch() throws Exception {
        // Given a patient, doctor, appointment and a medical act
        // and a invalid medical act id
        Patient patient = new Patient();
        patient.setPatientNumber("PAT001");
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patientRepository.save(patient);
        patient = patientRepository.findByPatientNumber(patient.getPatientNumber());

        Doctor doctor = new Doctor();
        doctor.setDoctorNumber("DOCTOR001");
        doctor.setFirstName("John");
        doctor.setLastName("Doe");
        doctorRepository.save(doctor);

        Appointment appointment = new Appointment();
        appointment.setAppointmentNumber("APPOINTMENT001");
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(LocalDateTime.now());
        appointment = appointmentRepository.save(appointment);

        doctor = doctorRepository.findByDoctorNumber(doctor.getDoctorNumber());
        doctor.addAppointment(appointment);
        doctorRepository.save(doctor);

        MedicalAct consultation = medicalActRepository.save(new MedicalAct("CONSULTATION", 10.0));
        Long invalidId = 999L;

        // When the process bill endpoint is called with an invalid medical act id
        server.perform(post("/bills")
                        .param("patientId", patient.getId().toString())
                        .param("doctorId", doctor.getId().toString())
                        .param("medicalActId", consultation.getId().toString(), invalidId.toString()))
                // Then
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.detail").value(containsString("Some medical act are not found: [999]")))
                .andExpect(jsonPath("$.detail").value(containsString(invalidId.toString())));
    }

    @Test
    void testProcessBill_ButNoneDontMatch() throws Exception {
        // Given a patient, doctor, appointment and a medical act and an invalid
        // medical act id
        Patient patient = new Patient();
        patient.setPatientNumber("PAT001");
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patientRepository.save(patient);
        patient = patientRepository.findByPatientNumber(patient.getPatientNumber());

        Doctor doctor = new Doctor();
        doctor.setDoctorNumber("DOCTOR001");
        doctor.setFirstName("John");
        doctor.setLastName("Doe");
        doctorRepository.save(doctor);

        Appointment appointment = new Appointment();
        appointment.setAppointmentNumber("APPOINTMENT001");
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(LocalDateTime.now());
        appointment = appointmentRepository.save(appointment);

        doctor = doctorRepository.findByDoctorNumber(doctor.getDoctorNumber());
        doctor.addAppointment(appointment);
        doctorRepository.save(doctor);

        Long invalidId = 999L;

        // When the process bill endpoint is called with an invalid medical act id
        server.perform(post("/bills")
                        .param("patientId", patient.getId().toString())
                        .param("doctorId", doctor.getId().toString())
                        .param("medicalActId", invalidId.toString()))
                // Then...
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.detail").value(containsString("Some medical act are not found: [999]")));
    }

    @Test
    void testProcessBill_ButOneIsInactive() throws Exception {
        // Given a patient, doctor, appointment and a medical act
        Patient patient = new Patient();
        patient.setPatientNumber("PAT001");
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patientRepository.save(patient);
        patient = patientRepository.findByPatientNumber(patient.getPatientNumber());

        Doctor doctor = new Doctor();
        doctor.setDoctorNumber("DOCTOR001");
        doctor.setFirstName("John");
        doctor.setLastName("Doe");
        doctorRepository.save(doctor);

        Appointment appointment = new Appointment();
        appointment.setAppointmentNumber("APPOINTMENT001");
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(LocalDateTime.now());
        appointment = appointmentRepository.save(appointment);

        doctor = doctorRepository.findByDoctorNumber(doctor.getDoctorNumber());
        doctor.addAppointment(appointment);
        doctorRepository.save(doctor);

        MedicalAct consultation = new MedicalAct("CONSULTATION", 10.0);
        consultation.setActive(false);
        consultation = medicalActRepository.save(consultation);

        // When the process bill endpoint is called with an inactive medical act
        server.perform(post("/bills")
                        .param("patientId", patient.getId().toString())
                        .param("doctorId", doctor.getId().toString())
                        .param("medicalActId", consultation.getId().toString()))
                // Then...
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.detail").value(containsString("Some medical acts are inactive")));
    }

    @Test
    void testCalculateInsurance() throws Exception {
        // Given an amount for a bill
        String amount = "1000.0";

        // When the calculate insurance endpoint is called
        server.perform(get("/bills/insurance-coverage")
                        .param("amount", amount))
                // Then...
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(amount));
    }

    @Test
    void testGetTotalRevenue() throws Exception {
        double initialTotalRevenue = billService.getTotalRevenue();
        // When the calculation of revenue endpoint is called
        server.perform(get("/bills/revenue"))
                // Then...
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRevenue").value(0.0 + initialTotalRevenue));
    }

    @Test
    void testGetPendingBills() throws Exception {
        Patient patient = patientRepository.save(new Patient("1", "John", "Doe"));
        Doctor doctor = doctorRepository.save(new Doctor("134", "Albert", "Martin"));

        // Given 3 Bill where two bill are pending
        Bill bill1 = new Bill();
        bill1.setStatus(Bill.Status.PENDING);
        bill1.setPatient(patient);
        bill1.setDoctor(doctor);
        bill1 = billRepository.save(bill1);

        Bill bill2 = new Bill();
        bill2.setStatus(Bill.Status.PAID);
        billRepository.save(bill2);

        Bill bill3 = new Bill();
        bill3.setStatus(Bill.Status.PENDING);
        bill3.setPatient(patient);
        bill3.setDoctor(doctor);
        bill3 = billRepository.save(bill3);

        when(billRepository.findBillsByStatus(Bill.Status.PENDING)).thenReturn(Arrays.asList(bill1, bill3));

        // When we fetch all pending bill
        server.perform(get("/bills/pending"))
                // Then two bills are return
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bills").isArray())
                .andExpect(jsonPath("$.bills.length()").value(2))
                .andExpect(jsonPath("$.bills[0].status").value(Bill.Status.PENDING.toString()))
                .andExpect(jsonPath("$.bills[1].status").value(Bill.Status.PENDING.toString()));
    }

    @Test
    void testCalculateInsuranceWithZeroAmount() throws Exception {
        // Given zero amount
        String amount = "0.0";
        // When the calculate insurance endpoint is called
        server.perform(get("/bills/insurance-coverage")
                        .param("amount", amount))
                // Then...
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(amount));
    }

    @Test
    void testTotalRevenueWithOneBill() throws Exception {
        double initialTotalRevenue = billService.getTotalRevenue();
        // Given a Bill with a total amount of 20
        BillDetail billDetail = new BillDetail(new MedicalAct("ACT1", 10.0), 2);
        Bill bill = billRepository.save(new Bill().addBillDetail(billDetail));

        assertEquals(20, bill.getTotalAmount());

        // When we get the revenue
        server.perform(get("/bills/revenue"))
                // Then the total revenue return is 20
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRevenue").value(20.0 + initialTotalRevenue));
    }

    @Test
    void testVerifyIntegrityBillOk() throws Exception {
        // Given a Bill hashed
        Bill bill = billService.processBill(
                new Patient("1", "John", "Doe"),
                new Doctor("134", "Albert", "Martin"),
                List.of(new MedicalAct("XRAY", 100),
                        new MedicalAct("XRAY", 100))
        );
        server.perform(get("/bills/verify-integrity")
                        .param("billNumber", bill.getBillNumber()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}