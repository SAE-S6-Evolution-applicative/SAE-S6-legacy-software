package sae.semestre.six.appointment.appointment;

import org.junit.jupiter.api.Test;
import sae.semestre.six.appointment.Appointment;
import sae.semestre.six.appointment.doctor.Doctor;
import sae.semestre.six.appointment.patient.Patient;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

class AppointmentTest {


    @Test
    void testSetAndGetDoctor() {
        // Given
        Appointment appointment = new Appointment();
        Doctor doctor = new Doctor();
        // When
        appointment.setDoctor(doctor);
        // Then
        assertEquals(doctor, appointment.getDoctor());
    }

    @Test
    void testSetAndGetPatient() {
        // Given
        Appointment appointment = new Appointment();
        Patient patient = new Patient();
        // When
        appointment.setPatient(patient);
        // Then
        assertEquals(patient, appointment.getPatient());
    }

    @Test
    void testSetAndGetStatus() {
        // Given
        Appointment appointment = new Appointment();
        // When
        appointment.setStatus("Scheduled");
        // Then
        assertEquals("Scheduled", appointment.getStatus());
    }
}
