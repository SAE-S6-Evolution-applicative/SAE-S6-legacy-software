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
import sae.semestre.six.appointment.AppointmentController.AvailableSlotRequestModel;
import sae.semestre.six.appointment.AppointmentController.ScheduleRequestModel;
import sae.semestre.six.appointment.AppointmentRepository;
import sae.semestre.six.appointment.AppointmentController;
import sae.semestre.six.appointment.doctor.Doctor;
import sae.semestre.six.appointment.doctor.DoctorRepository;
import sae.semestre.six.appointment.patient.Patient;
import sae.semestre.six.appointment.patient.PatientRepository;
import sae.semestre.six.common.SuccessfullResponseModel;
import sae.semestre.six.email.EmailService;
import sae.semestre.six.exception.ScheduleAlreadyTakenException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AppointmentControllerTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AppointmentController appointmentController;

    private Doctor doctor;
    private LocalDate validDate = LocalDate.now();
    private LocalDate invalidDate = LocalDate.now().minusDays(1);
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

        Patient patient = new Patient();
        patient.setId(1L);
        patient.setPatientNumber("123456789");
        patient.setFirstName("Dr.");
        patient.setLastName("Smith");
        patientRepository.save(patient);

        // Configure existing appointments
        existingAppointments = new ArrayList<>();
        LocalDateTime existingAppointmentDate = LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0));

        Appointment existingAppointment = new Appointment();
        existingAppointment.setAppointmentDate(existingAppointmentDate);
        existingAppointments.add(existingAppointment);

        // Replace EmailService instance with the mock
        ReflectionTestUtils.setField(appointmentController, "emailService", emailService);

        // Configure mocks
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findAllByDoctor_Id(1L)).thenReturn(existingAppointments);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
    }

    @Test
    void testScheduleAppointmentSuccess() {
        // When
        SuccessfullResponseModel result = appointmentController.scheduleAppointment(new ScheduleRequestModel(1L, 1L, validDate.atTime(12, 0)));

        // Then
        assertEquals(new SuccessfullResponseModel("Appointment correctly created", true), result);
        verify(emailService).sendEmail(
                eq("doctor@example.com"),
                eq("New Appointment Scheduled"),
                contains("You have a new appointment on")
        );
    }

    @Test
    void testScheduleAppointmentConflict() {
        // Given
        when(appointmentRepository.findAllByDoctor_Id(1L)).thenReturn(Collections.singletonList(
                createAppointment(validDate.atTime(10, 0))
        ));

        // When
        // Then
        assertThrows(ScheduleAlreadyTakenException.class,
                () -> appointmentController.scheduleAppointment(new ScheduleRequestModel(1L, 2L, validDate.atTime(2, 0)))
        );
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testGetAvailableSlots() {
        // When
        List<LocalDateTime> availableSlots = appointmentController.getAvailableSlots(new AvailableSlotRequestModel(1L, validDate));

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