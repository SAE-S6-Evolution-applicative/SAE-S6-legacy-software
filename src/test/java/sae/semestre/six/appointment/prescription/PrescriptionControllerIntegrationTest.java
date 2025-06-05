package sae.semestre.six.appointment.prescription;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import sae.semestre.six.appointment.bill.BillService;
import sae.semestre.six.appointment.patient.Patient;
import sae.semestre.six.appointment.patient.PatientRepository;
import sae.semestre.six.appointment.patient.PatientService;
import sae.semestre.six.appointment.prescription.PrescriptionController.PrescriptionRequest;
import sae.semestre.six.exception.EntityNotFoundException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
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
    private PrescriptionRepository prescriptionRepository;

    @MockitoBean
    private PatientService patientService;

    @Autowired
    private PrescriptionService prescriptionService;

    @MockitoBean
    private MedicineService medicineService;
    @MockitoSpyBean
    private PatientRepository patientRepository;
    @MockitoSpyBean
    private MedicineRepository medicineRepository;

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
        Patient patient = createTestPatient();
        Medicine medicine = new Medicine("Paracétamol", 5.0);
        medicine.setId(1L);
        Medicine medicine2 = new Medicine("Anxiolitics", 15.0);
        medicine2.setId(2L);

        String notes = "Take with food";

        when(patientService.getPatient(1L)).thenReturn(patient);
        when(medicineService.getByIds(List.of(1L, 2L))).thenReturn(List.of(medicine, medicine2));

        String json = """
        {
          "patientId": %d,
          "medicineIds": [1, 2],
          "notes": "%s"
        }
        """.formatted(patient.getId(), notes);

        server.perform(post("/prescriptions")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated());

        verify(patientService).getPatient(patient.getId());
        verify(prescriptionRepository).save(any(Prescription.class));
    }

    @Test
    void addPrescriptionButPatientAbsent() throws Exception {
        // Given a prescription with a wrong patient id
        Long nonExistentPatientId = 9999L;
        String notes = "Take with food";

        when(patientService.getPatient(nonExistentPatientId)).thenThrow(new EntityNotFoundException("Patient not found"));

        String json = """
        {
          "patientId": %d,
          "medicineIds": [1, 2],
          "notes": "%s"
        }
        """.formatted(nonExistentPatientId, notes);

        server.perform(post("/prescriptions")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPatientPrescriptions() throws Exception {
        // Arrange
        Long patientId = 1L;
        Patient patient = createTestPatient();

        when(patientService.getPatient(patientId)).thenReturn(patient);
        when(prescriptionService.findAllPrescriptionsByPatientId(patientId)).thenReturn(Collections.emptyList());

        // Act
        server.perform(get("/prescriptions/" + patientId))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("[]")); // Assuming no prescriptions exist
    }

    @Test
    void calculateCostTest() throws Exception {
        // Given a prescription
        Patient patient = new Patient();
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setPatientNumber("P12345");
        patient.setPhoneNumber("1234567890");
        patient.setGender("Male");
        patient.setDateOfBirth(LocalDate.now());
        patient.setAddress("123 Main St");
        patient = patientRepository.save(patient); // Persist patient correctly

        Medicine medicine = new Medicine("Paracétamol", 5.0);
        medicine = medicineRepository.save(medicine); // Persist medicine

        Medicine medicine2 = new Medicine("Anxiolitics", 15.0);
        medicine2 = medicineRepository.save(medicine2); // Persist second medicine

        List<Medicine> medicines = List.of(medicine, medicine2);

        String notes = "Take with food";
        Prescription prescription = new Prescription(1, patient, medicines, notes);

        // Persist the prescription
        prescription = prescriptionRepository.save(prescription);

        // When the total cost of the prescription is calculated
        double expectedCost = (5.0 + 15.0) * 1.2;

        server.perform(get("/prescriptions/" + prescription.getId() + "/cost"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(containsString(Double.toString(expectedCost))));
    }
}