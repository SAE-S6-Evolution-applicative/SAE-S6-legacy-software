/*
 * AppointmentDaoImplTest.java                                 13 mai 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.appointment;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import sae.semestre.six.appointment.Appointment;
import sae.semestre.six.appointment.AppointmentDao;
import sae.semestre.six.appointment.doctor.Doctor;
import sae.semestre.six.appointment.patient.Patient;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AppointmentDaoImplTest {

    @Autowired
    private AppointmentDao appointmentDao;

    @PersistenceContext
    private EntityManager entityManager;

    private Patient patient1;
    private Patient patient2;
    private Doctor doctor1;
    private Doctor doctor2;
    private Date today;
    private Date yesterday;
    private Date tomorrow;
    private Date nextWeek;

    @BeforeEach
    void setUp() {
        // Préparation des dates
        today = new Date();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        yesterday = cal.getTime();

        cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        tomorrow = cal.getTime();

        cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 7);
        nextWeek = cal.getTime();

        // Création des patients
        patient1 = new Patient();
        patient1.setFirstName("John");
        patient1.setLastName("Doe");
        patient1.setPatientNumber("PAT001");
        entityManager.persist(patient1);

        patient2 = new Patient();
        patient2.setFirstName("Jane");
        patient2.setLastName("Smith");
        patient2.setPatientNumber("PAT002");
        entityManager.persist(patient2);

        // Création des médecins
        doctor1 = new Doctor();
        doctor1.setFirstName("Dr.");
        doctor1.setLastName("House");
        doctor1.setDoctorNumber("DOC001");
        entityManager.persist(doctor1);

        doctor2 = new Doctor();
        doctor2.setFirstName("Dr.");
        doctor2.setLastName("Wilson");
        doctor2.setDoctorNumber("DOC002");
        entityManager.persist(doctor2);

        entityManager.flush();
    }

    /**
     * Helper method to create an appointment
     */
    private Appointment createAppointment(Patient patient, Doctor doctor, Date date, String appointmentNumber) {
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(date);
        appointment.setAppointmentNumber(appointmentNumber);
        appointment.setStatus("Scheduled");
        entityManager.persist(appointment);
        return appointment;
    }

    @Test
    void testFindByPatientId() {
        // Given
        Appointment appointment1 = createAppointment(patient1, doctor1, today, "APP001");
        Appointment appointment2 = createAppointment(patient1, doctor2, tomorrow, "APP002");
        Appointment appointment3 = createAppointment(patient2, doctor1, today, "APP003");
        entityManager.flush();

        // When
        List<Appointment> patient1Appointments = appointmentDao.findByPatientId(patient1.getId());
        List<Appointment> patient2Appointments = appointmentDao.findByPatientId(patient2.getId());

        // Then
        assertEquals(2, patient1Appointments.size());
        assertEquals(1, patient2Appointments.size());

        assertTrue(patient1Appointments.stream()
                .anyMatch(a -> a.getAppointmentNumber().equals("APP001")));
        assertTrue(patient1Appointments.stream()
                .anyMatch(a -> a.getAppointmentNumber().equals("APP002")));
        assertTrue(patient2Appointments.stream()
                .anyMatch(a -> a.getAppointmentNumber().equals("APP003")));
    }

    @Test
    void testFindByDoctorId() {
        // Given
        Appointment appointment1 = createAppointment(patient1, doctor1, today, "APP004");
        Appointment appointment2 = createAppointment(patient2, doctor1, tomorrow, "APP005");
        Appointment appointment3 = createAppointment(patient1, doctor2, today, "APP006");
        entityManager.flush();

        // When
        List<Appointment> doctor1Appointments = appointmentDao.findByDoctorId(doctor1.getId());
        List<Appointment> doctor2Appointments = appointmentDao.findByDoctorId(doctor2.getId());

        // Then
        assertEquals(2, doctor1Appointments.size());
        assertEquals(1, doctor2Appointments.size());

        assertTrue(doctor1Appointments.stream()
                .anyMatch(a -> a.getAppointmentNumber().equals("APP004")));
        assertTrue(doctor1Appointments.stream()
                .anyMatch(a -> a.getAppointmentNumber().equals("APP005")));
        assertTrue(doctor2Appointments.stream()
                .anyMatch(a -> a.getAppointmentNumber().equals("APP006")));
    }

    @Test
    void testFindByDateRange() {
        // Given
        Appointment appointment1 = createAppointment(patient1, doctor1, yesterday, "APP007");
        Appointment appointment2 = createAppointment(patient1, doctor2, today, "APP008");
        Appointment appointment3 = createAppointment(patient2, doctor1, tomorrow, "APP009");
        Appointment appointment4 = createAppointment(patient2, doctor2, nextWeek, "APP010");
        entityManager.flush();

        // When
        List<Appointment> currentAppointments = appointmentDao.findByDateRange(yesterday, tomorrow);
        List<Appointment> futureAppointments = appointmentDao.findByDateRange(tomorrow, nextWeek);

        // Then
        assertEquals(3, currentAppointments.size());
        assertEquals(2, futureAppointments.size());

        assertTrue(currentAppointments.stream()
                .anyMatch(a -> a.getAppointmentNumber().equals("APP007")));
        assertTrue(currentAppointments.stream()
                .anyMatch(a -> a.getAppointmentNumber().equals("APP008")));
        assertTrue(currentAppointments.stream()
                .anyMatch(a -> a.getAppointmentNumber().equals("APP009")));

        assertTrue(futureAppointments.stream()
                .anyMatch(a -> a.getAppointmentNumber().equals("APP009")));
        assertTrue(futureAppointments.stream()
                .anyMatch(a -> a.getAppointmentNumber().equals("APP010")));
    }
}