package sae.semestre.six.appointment.patient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sae.semestre.six.appointment.Appointment;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PatientTest {

    Patient patient;
    @BeforeEach
    void setUp() {
        patient = new Patient();
    }

    @Test
    void testSetAndGetId() {
        Long id = 1L;
        patient.setId(id);
        assertEquals(id, patient.getId());
    }

    @Test
    void testSetAndGetPatientNumber() {
        String patientNumber = "PN123456";
        patient.setPatientNumber(patientNumber);
        assertEquals(patientNumber, patient.getPatientNumber());
    }

    @Test
    void testSetAndGetFirstName() {
        String firstName = "John";
        patient.setFirstName(firstName);
        assertEquals(firstName, patient.getFirstName());
    }

    @Test
    void testSetAndGetLastName() {
        String lastName = "Doe";
        patient.setLastName(lastName);
        assertEquals(lastName, patient.getLastName());
    }

    @Test
    public void testSetDateOfBirth() {
        // Given
        Patient patient = new Patient();
        LocalDate dateOfBirth = LocalDate.now();
        
        // When
        patient.setDateOfBirth(dateOfBirth);
        
        // Then
        assertEquals(dateOfBirth, patient.getDateOfBirth());
    }

    @Test
    void testSetAndGetGender() {
        String gender = "Male";
        patient.setGender(gender);
        assertEquals(gender, patient.getGender());
    }

    @Test
    void testSetAndGetAddress() {
        String address = "123 Street, City";
        patient.setAddress(address);
        assertEquals(address, patient.getAddress());
    }

    @Test
    void testSetAndGetPhoneNumber() {
        String phoneNumber = "1234567890";
        patient.setPhoneNumber(phoneNumber);
        assertEquals(phoneNumber, patient.getPhoneNumber());
    }

    @Test
    void testSetAndGetAppointments() {
        Set<Appointment> appointments = new HashSet<>();
        Appointment appointment1 = new Appointment();
        Appointment appointment2 = new Appointment();
        appointments.add(appointment1);
        appointments.add(appointment2);
        patient.getAppointments().addAll(appointments);

        assertEquals(appointments.size(), patient.getAppointments().size());
    }

    @Test
    void testNoArgsConstructor() {
        Patient emptyPatient = new Patient();
        assertNotNull(emptyPatient);
    }
}