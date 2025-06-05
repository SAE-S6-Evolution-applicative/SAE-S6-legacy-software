/*
 * AppointmentControllerIntegrationTest.java                                 05 juin 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.appointment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import sae.semestre.six.appointment.AppointmentController;
import sae.semestre.six.appointment.AppointmentController.AvailableSlotRequestModel;
import sae.semestre.six.appointment.AppointmentController.ScheduleRequestModel;
import sae.semestre.six.appointment.AppointmentService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AppointmentController.class)
class AppointmentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppointmentService appointmentService;

    @Test
    void testScheduleAppointmentEndpoint() throws Exception {
        ScheduleRequestModel request = new ScheduleRequestModel(1L, 1L, LocalDateTime.of(2025, 5, 20, 12, 0));
        when(appointmentService.scheduleAppointment(request)).thenReturn(true);

        String json = """
            {
                "doctorId": 1,
                "patientId": 1,
                "appointmentDateTime": "2025-05-20T12:00:00"
            }
            """;

        mockMvc.perform(post("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Appointment correctly created"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testGetAvailableSlotsEndpoint() throws Exception {
        AvailableSlotRequestModel request = new AvailableSlotRequestModel(1L, LocalDate.of(2025, 5, 20));
        when(appointmentService.getAvailableTimeSlots(request)).thenReturn(Collections.singletonList(LocalDateTime.of(2025, 5, 20, 10, 0)));

        String json = """
            {
                "doctorId": 1,
                "date": "2025-05-20"
            }
            """;

        mockMvc.perform(get("/appointments/available-slots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("2025-05-20T10:00:00"));
    }
}
