/*
 * PatientHistoryDaoImplTest.java                                 13 mai 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.patient.history;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import sae.semestre.six.appointment.Appointment;
import sae.semestre.six.appointment.bill.Bill;
import sae.semestre.six.appointment.doctor.Doctor;
import sae.semestre.six.appointment.patient.Patient;
import sae.semestre.six.appointment.prescription.Prescription;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PatientHistoryDaoImplTest {

    @Autowired
    private PatientHistoryDao patientHistoryDao;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void testFindCompleteHistoryByPatientId() {
        // Given
        Patient patient = new Patient();
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setPatientNumber("12345");
        entityManager.persist(patient);

        // Create doctor, appointment etc. needed for the patient history

        PatientHistory history1 = createPatientHistory(patient, new Date(), "Fever");
        PatientHistory history2 = createPatientHistory(patient, new Date(), "Headache");

        entityManager.flush();
        entityManager.clear();

        // When
        List<PatientHistory> histories = patientHistoryDao.findCompleteHistoryByPatientId(patient.getId());

        // Then
        assertFalse(histories.isEmpty());
        assertEquals(2, histories.size());
    }

    @Test
    void testSearchByMultipleCriteria() {
        // Given
        Patient patient = new Patient();
        patient.setFirstName("Jane");
        patient.setLastName("Smith");
        patient.setPatientNumber("123456");
        entityManager.persist(patient);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -10);
        Date startDate = cal.getTime();

        cal.add(Calendar.DAY_OF_MONTH, 20);
        Date endDate = cal.getTime();

        cal.add(Calendar.DAY_OF_MONTH, -15);
        Date inRangeDate = cal.getTime();

        PatientHistory history1 = createPatientHistory(patient, inRangeDate, "Hypertension");

        Treatment treatment = new Treatment();
        treatment.setName("Anti-hypertensive medication");
        treatment.setPatientHistory(history1);
        entityManager.persist(treatment);

        Set<Treatment> treatments = (Set<Treatment>) ReflectionTestUtils.getField(history1, "treatments");
        treatments.add(treatment);
        ReflectionTestUtils.setField(history1, "treatments", treatments);

        entityManager.flush();
        entityManager.clear();

        // When
        List<PatientHistory> results = patientHistoryDao.searchByMultipleCriteria("hyper", startDate, endDate);

        // Then
        assertEquals(1, results.size());
        assertEquals(getId(history1), getId(results.get(0)));
    }

    private Long getId(PatientHistory history) {
        return (Long) ReflectionTestUtils.getField(history, "id");
    }

    private PatientHistory createPatientHistory(Patient patient, Date visitDate, String diagnosis) {
        PatientHistory history = new PatientHistory();
        ReflectionTestUtils.setField(history, "patient", patient);
        ReflectionTestUtils.setField(history, "visitDate", visitDate);
        ReflectionTestUtils.setField(history, "diagnosis", diagnosis);
        ReflectionTestUtils.setField(history, "symptoms", "Test symptoms");
        ReflectionTestUtils.setField(history, "notes", "Test notes");

        // Create required relationships
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setAppointmentDate(visitDate);
        appointment.setAppointmentNumber(RandomStringUtils.randomNumeric(6));
        ReflectionTestUtils.setField(appointment, "patientHistory", history);

        Doctor doctor = new Doctor();
        doctor.setFirstName("John");
        doctor.setLastName("Smith");
        doctor.setDoctorNumber(RandomStringUtils.randomNumeric(6));
        entityManager.persist(doctor);

        appointment.setDoctor(doctor);
        entityManager.persist(appointment);
        Set<Appointment> appointments = new HashSet<>();
        appointments.add(appointment);
        ReflectionTestUtils.setField(history, "appointments", appointments);

        Prescription prescription = new Prescription();
        ReflectionTestUtils.setField(prescription, "medicines", "Test medication");
        entityManager.persist(prescription);
        Set<Prescription> prescriptions = (Set<Prescription>) ReflectionTestUtils.getField(history, "prescriptions");
        prescriptions.add(prescription);
        ReflectionTestUtils.setField(history, "prescriptions", prescriptions);

        Treatment treatment = new Treatment();
        treatment.setName("Test treatment");
        treatment.setPatientHistory(history);
        entityManager.persist(treatment);

        Set<Treatment> treatments = (Set<Treatment>) ReflectionTestUtils.getField(history, "treatments");
        treatments.add(treatment);
        ReflectionTestUtils.setField(history, "treatments", treatments);

        Bill bill = new Bill();
        bill.setTotalAmount(100.0);
        bill.setBillDate(new Date());
        entityManager.persist(bill);

        Set<Bill> bills = (Set<Bill>) ReflectionTestUtils.getField(history, "bills");
        bills.add(bill);
        ReflectionTestUtils.setField(history, "bills", bills);

        LabResult labResult = new LabResult();
        labResult.setTestName("Test lab");
        labResult.setPatientHistory(history);
        entityManager.persist(labResult);

        Set<LabResult> labResults = (Set<LabResult>) ReflectionTestUtils.getField(history, "labResults");
        labResults.add(labResult);
        ReflectionTestUtils.setField(history, "labResults", labResults);

        entityManager.persist(history);
        return history;
    }
}