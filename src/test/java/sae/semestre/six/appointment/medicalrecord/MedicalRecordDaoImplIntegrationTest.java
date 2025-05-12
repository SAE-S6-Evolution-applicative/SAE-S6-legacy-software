/*
 * MedicalRecordDaoImplIntegrationTest.java                                 12 mai 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.medicalrecord;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import sae.semestre.six.appointment.doctor.Doctor;
import sae.semestre.six.appointment.patient.Patient;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MedicalRecordDaoImplIntegrationTest {

    @Autowired
    private MedicalRecordDao medicalRecordDao;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void testFindByRecordNumber() {
        // Given
        Patient patient = new Patient();
        patient.setPatientNumber("PAT002");
        patient.setFirstName("Jane");
        patient.setLastName("Doe");
        entityManager.persist(patient);

        MedicalRecord medicalRecord = createTestMedicalRecord("REC001", null, null);
        medicalRecord.setPatient(patient);
        entityManager.persist(medicalRecord);
        entityManager.flush();

        // When
        MedicalRecord foundRecord = medicalRecordDao.findByRecordNumber("REC001");

        // Then
        assertNotNull(foundRecord);
        assertEquals(medicalRecord.getRecordNumber(), foundRecord.getRecordNumber());
    }

    @Test
    void testFindByPatientId() {
        // Given
        Patient patient = new Patient();
        patient.setPatientNumber("PAT002");
        patient.setFirstName("Jane");
        patient.setLastName("Doe");
        entityManager.persist(patient);

        MedicalRecord medicalRecord = createTestMedicalRecord("REC002", patient, null);
        entityManager.persist(medicalRecord);
        entityManager.flush();

        // When
        List<MedicalRecord> records = medicalRecordDao.findByPatientId(patient.getId());

        // Then
        assertEquals(1, records.size());
        assertEquals(medicalRecord.getId(), records.get(0).getId());
    }

    @Test
    void testFindByDoctorId() {
        // Given
        Doctor doctor = new Doctor();
        doctor.setFirstName("Dr.");
        doctor.setLastName("Smith");
        doctor.setDoctorNumber("DOC002");
        entityManager.persist(doctor);

        Patient patient = new Patient();
        patient.setPatientNumber("PAT002");
        patient.setFirstName("Jane");
        patient.setLastName("Doe");
        entityManager.persist(patient);

        MedicalRecord medicalRecord = createTestMedicalRecord("REC003", null, doctor);
        medicalRecord.setPatient(patient);
        entityManager.persist(medicalRecord);
        entityManager.flush();

        // When
        List<MedicalRecord> records = medicalRecordDao.findByDoctorId(doctor.getId());

        // Then
        assertEquals(1, records.size());
        assertEquals(medicalRecord.getId(), records.get(0).getId());
    }

    @Test
    void testFindByDateRange() {
        // Given
        Date yesterday = addDaysToCurrentDate(-1);
        Date tomorrow = addDaysToCurrentDate(1);
        Date nextWeek = addDaysToCurrentDate(7);

        Patient patient = new Patient();
        patient.setPatientNumber("PAT002");
        patient.setFirstName("Jane");
        patient.setLastName("Doe");
        entityManager.persist(patient);

        MedicalRecord recordInRange = createTestMedicalRecord("REC004", null, null);
        recordInRange.setRecordDate(new Date()); // Today
        recordInRange.setPatient(patient);
        entityManager.persist(recordInRange);

        MedicalRecord recordOutOfRange = createTestMedicalRecord("REC005", null, null);
        recordOutOfRange.setRecordDate(nextWeek);
        recordOutOfRange.setPatient(patient);
        entityManager.persist(recordOutOfRange);

        entityManager.flush();

        // When
        List<MedicalRecord> records = medicalRecordDao.findByDateRange(yesterday, tomorrow);

        // Then
        assertEquals(1, records.size());
        assertEquals(recordInRange.getRecordNumber(), records.get(0).getRecordNumber());
    }

    @Test
    void testFindByDiagnosis() {
        // Given
        Patient patient = new Patient();
        patient.setPatientNumber("PAT002");
        patient.setFirstName("Jane");
        patient.setLastName("Doe");
        entityManager.persist(patient);

        MedicalRecord record1 = createTestMedicalRecord("REC006", null, null);
        record1.setDiagnosis("Hypertension");
        record1.setPatient(patient);
        entityManager.persist(record1);

        MedicalRecord record2 = createTestMedicalRecord("REC007", null, null);
        record2.setDiagnosis("Diabetes");
        record2.setPatient(patient);
        entityManager.persist(record2);

        MedicalRecord record3 = createTestMedicalRecord("REC008", null, null);
        record3.setDiagnosis("Hypertension with complications");
        record3.setPatient(patient);
        entityManager.persist(record3);

        entityManager.flush();

        // When
        List<MedicalRecord> records = medicalRecordDao.findByDiagnosis("Hyper");

        // Then
        assertEquals(2, records.size());
        assertTrue(records.stream().anyMatch(r -> r.getRecordNumber().equals("REC006")));
        assertTrue(records.stream().anyMatch(r -> r.getRecordNumber().equals("REC008")));
    }

    @Test
    void testFindByDiagnosisNoMatch() {
        // Given
        MedicalRecord record = createTestMedicalRecord("REC009", null, null);
        record.setDiagnosis("Migraine");

        Patient patient = new Patient();
        patient.setPatientNumber("PAT002");
        patient.setFirstName("Jane");
        patient.setLastName("Smith");
        entityManager.persist(patient);

        record.setPatient(patient);
        entityManager.persist(record);
        entityManager.flush();

        // When
        List<MedicalRecord> records = medicalRecordDao.findByDiagnosis("Cancer");

        // Then
        assertTrue(records.isEmpty());
    }

    /**
     * Helper method to create a test medical record
     */
    private MedicalRecord createTestMedicalRecord(String recordNumber, Patient patient, Doctor doctor) {
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setRecordNumber(recordNumber);
        medicalRecord.setPatient(patient);
        medicalRecord.setDoctor(doctor);
        medicalRecord.setDiagnosis("Test Diagnosis");
        medicalRecord.setTreatment("Test Treatment");
        medicalRecord.setRecordDate(new Date());
        return medicalRecord;
    }

    /**
     * Helper method to add days to the current date
     */
    private Date addDaysToCurrentDate(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }
}