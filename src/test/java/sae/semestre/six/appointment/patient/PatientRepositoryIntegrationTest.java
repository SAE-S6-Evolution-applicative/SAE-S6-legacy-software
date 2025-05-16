package sae.semestre.six.appointment.patient;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class PatientRepositoryIntegrationTest {

    @Autowired
    private PatientRepository patientRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void testFindByPatientNumber() {
        // Given
        Patient patient = new Patient();
        patient.setPatientNumber("PAT001");
        patient.setFirstName("John");
        patient.setLastName("Doe");
        entityManager.persist(patient);
        entityManager.flush();

        // When
        Patient foundPatient = patientRepository.findByPatientNumber("PAT001");

        // Then
        assertNotNull(foundPatient);
        assertEquals(patient.getPatientNumber(), foundPatient.getPatientNumber());
    }

    @Test
    void testFindAllByLastName() {
        // Given
        Patient patient = new Patient();
        patient.setLastName("Smith");
        patient.setFirstName("John");
        patient.setPatientNumber("PAT002");
        entityManager.persist(patient);
        entityManager.flush();

        // When
        List<Patient> patients = patientRepository.findAllByLastName("Smith");

        // Then
        assertEquals(1, patients.size());
        assertEquals(patient.getId(), patients.get(0).getId());
    }

}
