package sae.semestre.six.appointment.patient;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class PatientTest {

    @Test
    void testSetAndGetFirstName() {
        Patient patient = new Patient();
        patient.setFirstName("Alice");
        assertEquals("Alice", patient.getFirstName());
    }

    @Test
    void testSetAndGetLastName() {
        Patient patient = new Patient();
        patient.setLastName("Smith");
        assertEquals("Smith", patient.getLastName());
    }

    @Test
    void testSetAndGetPatientNumber() {
        Patient patient = new Patient();
        patient.setPatientNumber("PAT001");
        assertEquals("PAT001", patient.getPatientNumber());
    }

    @Test
    void testSetAndGetDateOfBirth() {
        Patient patient = new Patient();
        Date dob = new Date();
        patient.setDateOfBirth(dob);
        assertEquals(dob, patient.getDateOfBirth());
    }

    @Test
    void testSetAndGetPhoneNumber() {
        Patient patient = new Patient();
        patient.setPhoneNumber("123-456-7890");
        assertEquals("123-456-7890", patient.getPhoneNumber());
    }

}
