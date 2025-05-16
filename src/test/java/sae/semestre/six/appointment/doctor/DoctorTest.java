package sae.semestre.six.appointment.doctor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DoctorTest {

    @Test
    void testSetAndGetFirstName() {
        Doctor doctor = new Doctor();
        doctor.setFirstName("John");
        assertEquals("John", doctor.getFirstName());
    }

    @Test
    void testSetAndGetLastName() {
        Doctor doctor = new Doctor();
        doctor.setLastName("Doe");
        assertEquals("Doe", doctor.getLastName());
    }

    @Test
    void testSetAndGetDoctorNumber() {
        Doctor doctor = new Doctor();
        doctor.setDoctorNumber("DOC001");
        assertEquals("DOC001", doctor.getDoctorNumber());
    }

    @Test
    void testSetAndGetSpecialization() {
        Doctor doctor = new Doctor();
        doctor.setSpecialization("Cardiology");
        assertEquals("Cardiology", doctor.getSpecialization());
    }

    @Test
    void testSetAndGetEmail() {
        Doctor doctor = new Doctor();
        doctor.setEmail("john.doe@example.com");
        assertEquals("john.doe@example.com", doctor.getEmail());
    }
}
