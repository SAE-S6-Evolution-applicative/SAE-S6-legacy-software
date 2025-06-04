/*
 * SchedulingController.java                                  19 mai. 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sae.semestre.six.common.SuccessfullResponseModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/appointments")
@Tag(name = "Scheduling", description = "Appointment management API")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Autowired
    public AppointmentController(final AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Operation(summary = "Schedule an appointment", description = "Creates a new appointment between a doctor and a patient")
    @ApiResponse(responseCode = "200", description = "Appointment scheduled successfully")
    @ApiResponse(responseCode = "400", description = "Invalid data or time conflict")
    @PostMapping
    public SuccessfullResponseModel scheduleAppointment(
            @Parameter(description = "Information needed to schedule an appointment")
            @RequestBody ScheduleRequestModel scheduleRequestModel
    ) {
        return new SuccessfullResponseModel("Appointment correctly created", appointmentService.scheduleAppointment(scheduleRequestModel));
    }


    @Operation(summary = "Get available slots", description = "Retrieves all available time slots for a doctor on a given date")
    @ApiResponse(responseCode = "200", description = "List of available slots")
    @GetMapping("/available-slots")
    public List<LocalDateTime> getAvailableSlots(
            @Parameter(description = "Information needed to search for available slots")
            @RequestBody AvailableSlotRequestModel availableSlotRequestModel) {

        return appointmentService.getAvailableTimeSlots(availableSlotRequestModel);
    }

    /**
     * Request model to schedule an appointment.
     *
     * @param doctorId           the unique identifier of the doctor
     * @param patientId          the unique identifier of the patient
     * @param appointmentDateTime the date and time of the appointment, it should not include precise minutes but only hours
     */
    public record ScheduleRequestModel(Long doctorId, Long patientId, LocalDateTime appointmentDateTime) {}

    public record AvailableSlotRequestModel(Long doctorId, LocalDate date) {}

} 