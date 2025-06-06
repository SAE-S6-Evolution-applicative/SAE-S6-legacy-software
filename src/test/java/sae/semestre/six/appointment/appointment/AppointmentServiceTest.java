/*
 * AppointmentServiceTest.java                                 04 juin 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.appointment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;
import sae.semestre.six.appointment.Appointment;
import sae.semestre.six.appointment.AppointmentController.AvailableSlotRequestModel;
import sae.semestre.six.appointment.AppointmentController.ScheduleRequestModel;
import sae.semestre.six.appointment.AppointmentRepository;
import sae.semestre.six.appointment.AppointmentService;
import sae.semestre.six.appointment.doctor.Doctor;
import sae.semestre.six.appointment.doctor.DoctorService;
import sae.semestre.six.appointment.patient.Patient;
import sae.semestre.six.appointment.patient.PatientService;
import sae.semestre.six.email.EmailService;
import sae.semestre.six.exception.EntityNotFoundException;
import sae.semestre.six.exception.ScheduleAlreadyTakenException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class AppointmentServiceTest {

    @MockitoBean
    private AppointmentRepository appointmentRepository;
    @MockitoBean
    private DoctorService doctorService;
    @MockitoBean
    private PatientService patientService;
    @MockitoSpyBean
    private EmailService emailService;

    @Autowired
    private AppointmentService appointmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAppointmentById_shouldReturnAppointment_whenFound() {
        Appointment appointment = mock(Appointment.class);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        Appointment result = appointmentService.findAppointmentById(1L);

        assertEquals(appointment, result);
    }

    @Test
    void findAppointmentById_shouldThrow_whenNotFound() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> appointmentService.findAppointmentById(1L));
    }

    @Test
    void scheduleAppointment_shouldSaveAndSendEmail_whenNoConflict() {
        Doctor doctor = mock(Doctor.class);
        String doctorEmail = "doctor@hospital.com";
        when(doctor.getEmail()).thenReturn(doctorEmail);
        Patient patient = mock(Patient.class);
        LocalDateTime dateTime = LocalDateTime.of(2025, 5, 22, 10, 0);
        ScheduleRequestModel req = new ScheduleRequestModel(1L, 2L, dateTime);

        when(doctorService.getDoctor(eq(req.doctorId()))).thenReturn(doctor);
        when(patientService.getPatient(eq(req.patientId()))).thenReturn(patient);
        when(appointmentRepository.findAllByDoctor_Id(1L)).thenReturn(Collections.emptyList());

        boolean result = appointmentService.scheduleAppointment(req);

        assertTrue(result);
        verify(appointmentRepository).save(any(Appointment.class));
        verify(emailService).sendEmail(eq(doctorEmail), anyString(), anyString());
    }

    @Test
    void scheduleAppointment_shouldThrow_whenConflict() {
        Doctor doctor = mock(Doctor.class);
        Patient patient = mock(Patient.class);
        LocalDateTime dateTime = LocalDateTime.of(2025, 5, 22, 10, 0);
        ScheduleRequestModel req = new ScheduleRequestModel(1L, 2L, dateTime);

        Appointment existing = mock(Appointment.class);
        when(existing.getAppointmentDate()).thenReturn(dateTime);
        when(doctorService.getDoctor(eq(req.doctorId()))).thenReturn(doctor);
        when(patientService.getPatient(eq(req.patientId()))).thenReturn(patient);
        when(appointmentRepository.findAllByDoctor_Id(1L)).thenReturn(List.of(existing));
        when(existing.hasTimeConflict(dateTime)).thenReturn(true);

        assertThrows(ScheduleAlreadyTakenException.class, () -> appointmentService.scheduleAppointment(req));
    }

    @Test
    void getAvailableTimeSlots_shouldReturnAvailableSlots() {
        LocalDate date = LocalDate.of(2025, 5, 22);
        Long doctorId = 1L;
        AvailableSlotRequestModel req = new AvailableSlotRequestModel(doctorId, date);

        Appointment taken = mock(Appointment.class);
        when(taken.getAppointmentDate()).thenReturn(LocalDateTime.of(date, java.time.LocalTime.of(Appointment.SCHEDULE_START_HOUR, 0)));
        when(appointmentRepository.findAllByDoctor_IdAndAppointmentDateBetween(
                eq(doctorId),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(List.of(taken));

        List<LocalDateTime> slots = appointmentService.getAvailableTimeSlots(req);

        assertFalse(slots.contains(LocalDateTime.of(date, java.time.LocalTime.of(Appointment.SCHEDULE_START_HOUR, 0))));
    }
}
