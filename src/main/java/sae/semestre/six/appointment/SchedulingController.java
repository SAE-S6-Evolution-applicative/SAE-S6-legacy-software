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
import sae.semestre.six.appointment.doctor.Doctor;
import sae.semestre.six.appointment.doctor.DoctorRepository;
import sae.semestre.six.appointment.patient.PatientRepository;
import sae.semestre.six.email.EmailService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/appointments")
@Tag(name = "Scheduling", description = "Appointment management API")
public class SchedulingController {

    public static final int SCHEDULE_START_HOUR = 9;

    public static final int SCHEDULE_STOP_HOUR = 17;

    AppointmentRepository appointmentRepository;

    DoctorRepository doctorRepository;

    PatientRepository patientRepository;

    private EmailService emailService;

    @Autowired
    public SchedulingController(AppointmentRepository appointmentRepository, DoctorRepository doctorRepository, PatientRepository patientRepository, EmailService emailService) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.emailService = emailService;
    }

    @Operation(summary = "Schedule an appointment", description = "Creates a new appointment between a doctor and a patient")
    @ApiResponse(responseCode = "200", description = "Appointment scheduled successfully")
    @ApiResponse(responseCode = "400", description = "Invalid data or time conflict")
    @PostMapping
    public String scheduleAppointment(
            @Parameter(description = "Doctor ID") @RequestParam Long doctorId,
            @Parameter(description = "Patient ID") @RequestParam Long patientId,
            @Parameter(description = "Appointment date and time") @RequestParam LocalDateTime appointmentDateTime) {
        try {
            Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(
                    () -> new RuntimeException("Doctor not found")
            );

            // Retrieve all appointments for the doctor
            List<Appointment> doctorAppointments = appointmentRepository.findAllByDoctor_Id(doctorId);

            // Check for time conflict
            boolean conflict = doctorAppointments.stream().anyMatch(existing -> existing.getAppointmentDate().equals(appointmentDateTime));

            if (conflict) {
                return "Doctor is not available at this time";
            }

            // Check if the appointment is within working hours
            int hour = appointmentDateTime.getHour();
            if (hour < SCHEDULE_START_HOUR || hour > SCHEDULE_STOP_HOUR) {
                return "Appointments only available between 9 AM and 5 PM";
            }

            // schedule appointment
            Appointment appointment = new Appointment();
            appointment.setDoctor(doctor);
            appointment.setAppointmentDate(appointmentDateTime);
            appointment.setPatient(patientRepository.findById(patientId).orElseThrow(
                    () -> new RuntimeException("Patient not found")
            ));
            appointmentRepository.save(appointment);

            // Send email notification
            emailService.sendEmail(
                    doctor.getEmail(),
                    "New Appointment Scheduled",
                    "You have a new appointment on " + appointmentDateTime
            );

            return "Appointment scheduled successfully";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }


    @Operation(summary = "Get available slots", description = "Retrieves all available time slots for a doctor on a given date")
    @ApiResponse(responseCode = "200", description = "List of available slots")
    @GetMapping("/available-slots")
    public List<LocalDateTime> getAvailableSlots(
            @Parameter(description = "Doctor ID") @RequestParam Long doctorId,
            @Parameter(description = "Date to check for available slots") @RequestParam LocalDate date) {

        List<LocalDateTime> availableSlots = new ArrayList<>();

        // Retrieve all appointments for the doctor
        List<Appointment> appointments = appointmentRepository.findAllByDoctor_Id(doctorId);

        // Iterate through hours from 9 AM to 5 PM
        for (int hour = SCHEDULE_START_HOUR; hour <= SCHEDULE_STOP_HOUR; hour++) {
            LocalDateTime slot = LocalDateTime.of(date, LocalTime.of(hour, 0));

            // Check if this slot conflicts with any existing appointment
            boolean isTaken = appointments.stream().anyMatch(app -> {
                LocalDateTime appDateTime = app.getAppointmentDate();
                return appDateTime.getYear() == slot.getYear()
                        && appDateTime.getDayOfYear() == slot.getDayOfYear()
                        && appDateTime.getHour() == slot.getHour();
            });

            if (!isTaken) {
                availableSlots.add(slot);
            }
        }

        return availableSlots;
    }

} 