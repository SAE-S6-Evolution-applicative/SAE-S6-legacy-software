/*
 * SchedulingControllerUnitTest.java                            13 May 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.appointment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sae.semestre.six.appointment.AppointmentController;
import sae.semestre.six.appointment.AppointmentController.AvailableSlotRequestModel;
import sae.semestre.six.appointment.AppointmentController.ScheduleRequestModel;
import sae.semestre.six.appointment.AppointmentService;
import sae.semestre.six.common.SuccessfullResponseModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentControllerUnitTest {

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private AppointmentController appointmentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testScheduleAppointmentSuccess() {
        ScheduleRequestModel request = new ScheduleRequestModel(1L, 1L, LocalDateTime.now());
        when(appointmentService.scheduleAppointment(request)).thenReturn(true);

        SuccessfullResponseModel result = appointmentController.scheduleAppointment(request);

        assertEquals(new SuccessfullResponseModel("Appointment correctly created", true), result);
        verify(appointmentService).scheduleAppointment(request);
    }

    @Test
    void testGetAvailableSlots() {
        AvailableSlotRequestModel request = new AvailableSlotRequestModel(1L, LocalDate.now());
        List<LocalDateTime> slots = Collections.singletonList(LocalDateTime.now());
        when(appointmentService.getAvailableTimeSlots(request)).thenReturn(slots);

        List<LocalDateTime> result = appointmentController.getAvailableSlots(request);

        assertEquals(slots, result);
        verify(appointmentService).getAvailableTimeSlots(request);
    }
}