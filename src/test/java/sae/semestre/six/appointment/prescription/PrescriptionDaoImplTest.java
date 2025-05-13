package sae.semestre.six.appointment.prescription;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import sae.semestre.six.appointment.patient.Patient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class PrescriptionDaoImplTest {

    @Autowired
    private PrescriptionDaoImpl prescriptionDao;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void testFindByPatientId_ReturnsPrescriptions_WhenPatientExists() {
        Patient patient = new Patient();
        patient.setPatientNumber("PAT001");
        patient.setFirstName("John");
        patient.setLastName("Doe");
        
        entityManager.persist(patient);

        Prescription prescription = new Prescription();
        prescription.setPrescriptionNumber("RX123");
        prescription.setPatient(patient);
        entityManager.persist(prescription);
        entityManager.flush();

        Long patientId = patient.getId();
        List<Prescription> prescriptions = prescriptionDao.findByPatientId(patientId);

        assertEquals(1, prescriptions.size());
        assertEquals("RX123", prescriptions.get(0).getPrescriptionNumber());
    }

    @Test
    void testFindByPatientId_ReturnsEmptyList_WhenPatientHasNoPrescriptions() {
        Long patientId = 2L;

        List<Prescription> prescriptions = prescriptionDao.findByPatientId(patientId);

        assertEquals(0, prescriptions.size());
        assertTrue(prescriptions.isEmpty());
    }

    @Test
    void testFindByPatientId_ReturnsEmptyList_WhenPatientDoesNotExist() {
        Long patientId = 99L;

        List<Prescription> prescriptions = prescriptionDao.findByPatientId(patientId);

        assertEquals(0, prescriptions.size());
        assertTrue(prescriptions.isEmpty());
    }
}