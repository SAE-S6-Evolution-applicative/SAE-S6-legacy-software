/*
 * SchedulingControllerTest.java                                 13 May 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.appointment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import sae.semestre.six.appointment.Appointment;
import sae.semestre.six.appointment.AppointmentDao;
import sae.semestre.six.appointment.SchedulingController;
import sae.semestre.six.appointment.doctor.Doctor;
import sae.semestre.six.appointment.doctor.DoctorDao;
import sae.semestre.six.email.EmailService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SchedulingControllerTest {

    @Mock
    private AppointmentDao appointmentDao;

    @Mock
    private DoctorDao doctorDao;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private SchedulingController schedulingController;

    private Doctor doctor;
    private Date validDate;
    private Date invalidDate;
    private List<Appointment> existingAppointments;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create a doctor for testing
        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setEmail("doctor@example.com");
        doctor.setFirstName("Dr.");
        doctor.setLastName("Smith");

        // Configure a valid date (10 AM)
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 10);
        cal.set(Calendar.MINUTE, 0);
        validDate = cal.getTime();

        // Configure an invalid date (8 PM)
        cal.set(Calendar.HOUR_OF_DAY, 20);
        invalidDate = cal.getTime();

        // Configure existing appointments
        existingAppointments = new ArrayList<>();
        cal.set(Calendar.HOUR_OF_DAY, 11);
        Date existingAppointmentDate = cal.getTime();

        Appointment existingAppointment = new Appointment();
        existingAppointment.setAppointmentDate(existingAppointmentDate);
        existingAppointments.add(existingAppointment);

        // Replace EmailService instance with the mock
        ReflectionTestUtils.setField(schedulingController, "emailService", emailService);

        // Configure mocks
        when(doctorDao.findById(1L)).thenReturn(doctor);
        when(appointmentDao.findByDoctorId(1L)).thenReturn(existingAppointments);
    }

    @Test
    void testScheduleAppointmentSuccess() {
        // When
        String result = schedulingController.scheduleAppointment(1L, 2L, validDate);

        // Then
        assertEquals("Appointment scheduled successfully", result);
        verify(emailService).sendEmail(
                eq("doctor@example.com"),
                eq("New Appointment Scheduled"),
                contains("You have a new appointment on")
        );
    }

    @Test
    void testScheduleAppointmentOutOfHours() {
        // When
        String result = schedulingController.scheduleAppointment(1L, 2L, invalidDate);

        // Then
        assertEquals("Appointments only available between 9 AM and 5 PM", result);
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testScheduleAppointmentConflict() {
        // Given
        when(appointmentDao.findByDoctorId(1L)).thenReturn(Collections.singletonList(
                createAppointment(validDate)
        ));

        // When
        String result = schedulingController.scheduleAppointment(1L, 2L, validDate);

        // Then
        assertEquals("Doctor is not available at this time", result);
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testGetAvailableSlots() {
        // When
        List<Date> availableSlots = schedulingController.getAvailableSlots(1L, validDate);

        // Then
        assertEquals(8, availableSlots.size()); // 9 AM-5 PM minus the 11 AM appointment = 8 slots

        // Verify that the 11 AM slot is not available
        Calendar cal = Calendar.getInstance();
        boolean has11AMSlot = false;

        for (Date slot : availableSlots) {
            cal.setTime(slot);
            if (cal.get(Calendar.HOUR_OF_DAY) == 11) {
                has11AMSlot = true;
                break;
            }
        }

        assertFalse(has11AMSlot);
    }

    private Appointment createAppointment(Date date) {
        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(date);
        return appointment;
    }
}