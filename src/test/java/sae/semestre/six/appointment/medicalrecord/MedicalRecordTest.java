package sae.semestre.six.appointment.medicalrecord;

import org.junit.jupiter.api.Test;
import sae.semestre.six.appointment.doctor.Doctor;
import sae.semestre.six.appointment.patient.Patient;

import static org.junit.jupiter.api.Assertions.*;

class MedicalRecordTest {

    @Test
    void testSetAndGetDiagnosis() {
        MedicalRecord record = new MedicalRecord();
        record.setDiagnosis("Flu");
        assertEquals("Flu", record.getDiagnosis());
    }

    @Test
    void testSetAndGetRecordNumber() {
        MedicalRecord record = new MedicalRecord();
        record.setRecordNumber("REC123");
        assertEquals("REC123", record.getRecordNumber());
    }

    @Test
    void testSetAndGetPatient() {
        MedicalRecord record = new MedicalRecord();
        Patient patient = new Patient();
        record.setPatient(patient);
        assertEquals(patient, record.getPatient());
    }

    @Test
    void testSetAndGetDoctor() {
        MedicalRecord record = new MedicalRecord();
        Doctor doctor = new Doctor();
        record.setDoctor(doctor);
        assertEquals(doctor, record.getDoctor());
    }

    @Test
    void testSetAndGetBloodPressure() {
        MedicalRecord record = new MedicalRecord();
        record.setBloodPressure("120/80");
        assertEquals("120/80", record.getBloodPressure());
    }

}
