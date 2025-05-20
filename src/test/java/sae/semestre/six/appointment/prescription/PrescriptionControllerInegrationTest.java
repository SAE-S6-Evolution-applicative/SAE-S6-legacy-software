package sae.semestre.six.appointment.prescription;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
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


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@AutoConfigureMockMvc
class PrescriptionControllerIntegrationTest {

    @Autowired
    private MockMvc server;

    @MockitoSpyBean
    private PrescriptionController prescriptionController;

    @MockitoBean
    private BillService billService;

    @MockitoBean
    private PatientRepository patientRepository;

    @MockitoBean
    private PrescriptionRepository prescriptionRepository;

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