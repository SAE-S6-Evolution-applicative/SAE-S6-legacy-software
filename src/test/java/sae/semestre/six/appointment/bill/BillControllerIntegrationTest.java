package sae.semestre.six.appointment.bill;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
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

import java.time.LocalDateTime;

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

    private static final Logger log = LoggerFactory.getLogger(BillControllerIntegrationTest.class);

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("test", "test"));

    @Autowired
    private MockMvc server;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private BillService billService;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private MedicalActService medicalActService;

    @Autowired
    private MedicalActRepository medicalActRepository;

    @Autowired
    private BillDetailRepository billDetailRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private EmailService emailService;

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
        assertEquals(consultation, billDetail.getMedicalAct());
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
        // When the calculation of revenue endpoint is called
        server.perform(get("/bills/revenue"))
                // Then...
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRevenue").value(0.0));
    }

    @Test
    void testGetPendingBills() throws Exception {
        // Given 3 Bill where two bill are pending
        Bill bill1 = new Bill();
        bill1.setStatus(Bill.Status.PENDING);
        bill1 = billRepository.save(bill1);

        Bill bill2 = new Bill();
        bill2.setStatus(Bill.Status.PAID);
        billRepository.save(bill2);

        Bill bill3 = new Bill();
        bill3.setStatus(Bill.Status.PENDING);
        bill3 = billRepository.save(bill3);

        // When we fetch all pending bill
        server.perform(get("/bills/pending"))
                // Then two bills are return
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pendingBills").isArray())
                .andExpect(jsonPath("$.pendingBills.length()").value(2))
                .andExpect(jsonPath("$.pendingBills").value(hasItems(
                        bill1.getId().toString(),
                        bill3.getId().toString()
                )));
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
        // Given a Bill with a total amount of 20
        MedicalAct medicalAct = medicalActRepository.save(new MedicalAct("ACT1", 10.0));
        Bill bill = billRepository.save(new Bill());
        BillDetail billDetail = billDetailRepository.save(new BillDetail(bill, medicalAct, 2));

        bill = billRepository.save(bill.addBillDetail(billDetail));

        assertEquals(20, bill.getTotalAmount());

        // When we get the revenue
        server.perform(get("/bills/revenue"))
                // Then te total revenue return is 20
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRevenue").value(20.0));
    }
}
