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
import sae.semestre.six.appointment.AppointmentRepository;
import sae.semestre.six.appointment.SchedulingController;
import sae.semestre.six.appointment.doctor.Doctor;
import sae.semestre.six.appointment.doctor.DoctorDao;
import sae.semestre.six.appointment.patient.PatientDao;
import sae.semestre.six.email.EmailService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SchedulingControllerTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorDao doctorDao;

    @Mock
    private PatientDao patientDao;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private SchedulingController schedulingController;

    private Doctor doctor;
    private LocalDate validDate = LocalDate.now();
    private LocalDate invalidDate = LocalDate.now();
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

        // Configure existing appointments
        existingAppointments = new ArrayList<>();
        LocalDateTime existingAppointmentDate = LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0));

        Appointment existingAppointment = new Appointment();
        existingAppointment.setAppointmentDate(existingAppointmentDate);
        existingAppointments.add(existingAppointment);

        // Replace EmailService instance with the mock
        ReflectionTestUtils.setField(schedulingController, "emailService", emailService);

        // Configure mocks
        when(doctorDao.findById(1L)).thenReturn(doctor);
        when(appointmentRepository.findAllByDoctor_Id(1L)).thenReturn(existingAppointments);
    }

    @Test
    void testScheduleAppointmentSuccess() {
        // When
        String result = schedulingController.scheduleAppointment(1L, 2L, validDate.atTime(12, 0) );

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
        String result = schedulingController.scheduleAppointment(1L, 2L, validDate.atTime(2, 0));

        // Then
        assertEquals("Appointments only available between 9 AM and 5 PM", result);
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testScheduleAppointmentConflict() {
        // Given
        when(appointmentRepository.findAllByDoctor_Id(1L)).thenReturn(Collections.singletonList(
                createAppointment(validDate.atTime(10, 0))
        ));

        // When
        String result = schedulingController.scheduleAppointment(1L, 2L, validDate.atTime(10, 0));

        // Then
        assertEquals("Doctor is not available at this time", result);
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testGetAvailableSlots() {
        // When
        List<LocalDateTime> availableSlots = schedulingController.getAvailableSlots(1L, validDate);

        // Then
        assertEquals(8, availableSlots.size());

        // Verify that the 10 AM slot is not available
        boolean has10AMSlot = false;

        for (LocalDateTime slot : availableSlots) {
            if (slot.getHour() == 10) {
                has10AMSlot = true;
                break;
            }
        }

        assertFalse(has10AMSlot);
    }

    private Appointment createAppointment(LocalDateTime date) {
        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(date);
        return appointment;
    }
}