/*
 * PrescriptionControllerIntegrationTest.java                                 05 juin 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.prescription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import sae.semestre.six.appointment.patient.Patient;
import sae.semestre.six.appointment.patient.PatientRepository;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@AutoConfigureMockMvc
class PrescriptionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    private Patient patient;
    private Medicine med1, med2;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setPatientNumber("P12345");
        patient.setPhoneNumber("1234567890");
        patient.setGender("Male");
        patient.setDateOfBirth(LocalDate.now());
        patient.setAddress("123 Main St");
        patient = patientRepository.save(patient);

        med1 = new Medicine("Paracetamol", 10.0);
        med2 = new Medicine("Ibuprofen", 15.0);
        med1 = medicineRepository.save(med1);
        med2 = medicineRepository.save(med2);
    }

    @Test
    void addPrescription() throws Exception {
        String json = """
        {
          "patientId": %d,
          "medicineIds": [%d, %d],
          "notes": "Take with food"
        }
        """.formatted(patient.getId(), med1.getId(), med2.getId());

        mockMvc.perform(post("/prescriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        List<Prescription> prescriptions = prescriptionRepository.findAll();
        assertThat(prescriptions).isNotEmpty();
        assertThat(prescriptions.get(0).getPatient().getId()).isEqualTo(patient.getId());
    }

    @Test
    void addPrescriptionButPatientAbsent() throws Exception {
        String json = """
        {
          "patientId": 9999,
          "medicineIds": [%d, %d],
          "notes": "Take with food"
        }
        """.formatted(med1.getId(), med2.getId());

        mockMvc.perform(post("/prescriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPatientPrescriptions() throws Exception {
        Prescription prescription = new Prescription(1, patient, List.of(med1, med2), "notes");
        prescriptionRepository.save(prescription);

        mockMvc.perform(get("/prescriptions/" + patient.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(prescription.getId()));
    }

    @Test
    void calculateCost() throws Exception {
        Prescription prescription = new Prescription(1, patient, List.of(med1, med2), "");
        prescription = prescriptionRepository.save(prescription);

        mockMvc.perform(get("/prescriptions/" + prescription.getId() + "/cost"))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf((med1.getUnitPrice() + med2.getUnitPrice()) * 1.2)));
    }
}