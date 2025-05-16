package sae.semestre.six.appointment.prescription;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import sae.semestre.six.appointment.bill.BillService;
import sae.semestre.six.appointment.patient.Patient;
import sae.semestre.six.appointment.patient.PatientRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(PrescriptionController.class)
class PrescriptionControllerInegrationTest {

    private MockMvc server;

    @InjectMocks
    private PrescriptionController prescriptionController;

    @MockitoBean
    private BillService billService;
    @MockitoBean
    private PatientRepository patientRepository;
    @MockitoBean
    private PrescriptionRepository prescriptionRepository;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        server = MockMvcBuilders.standaloneSetup(prescriptionController).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    private Patient createTestPatient() {
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setPatientNumber("P12345");
        patient.setPhoneNumber("1234567890");
        patient.setGender("Male");
        patient.setDateOfBirth(LocalDate.now());
        patient.setAddress("123 Main St");
        return patient;
    }

    @Test
    void addPrescription() throws Exception {
        int counter = 1;
        Patient patient = createTestPatient();
        String[] medicines = {"PARACETAMOL", "ANTIBIOTICS"};
        String notes = "Take with food";

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        server.perform(post("/prescriptions")
                        .param("patientId", patient.getId().toString())
                        .param("medicines", medicines)
                        .param("notes", notes))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Prescription RX" + counter + " created and billed"));

        verify(patientRepository).findById(patient.getId());
        verify(prescriptionRepository).save(any(Prescription.class));
        verify(billService).processBill(
                patient.getId().toString(),
                "SYSTEM",
                new String[]{"PRESCRIPTION_RX" + counter}
        );
    }

    @Test
    void addPrescriptionButPatientAbsent() throws Exception {
        Long nonExistentPatientId = 9999L;
        String[] medicines = {"PARACETAMOL", "ANTIBIOTICS"};
        String notes = "Take with food";

        when(patientRepository.findById(nonExistentPatientId)).thenThrow(new RuntimeException("Patient not found"));

        server.perform(post("/prescriptions")
                        .param("patientId", String.valueOf(nonExistentPatientId))
                        .param("medicines", medicines)
                        .param("notes", notes))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(containsString("Failed")));
    }

    @Test
    void getPatientPrescriptions() throws Exception {
        // Arrange
        String patientId = "1";
        Patient patient = createTestPatient();
        when(patientRepository.findById(Long.parseLong(patientId))).thenReturn(Optional.of(patient));

        // Act
        server.perform(get("/prescriptions/patient/" + patientId))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("[]")); // Assuming no prescriptions exist
    }
}