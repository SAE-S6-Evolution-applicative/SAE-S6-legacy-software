package sae.semestre.six.appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sae.semestre.six.appointment.doctor.DoctorDao;
import sae.semestre.six.appointment.doctor.Doctor;
import sae.semestre.six.appointment.patient.PatientDao;
import sae.semestre.six.appointment.patient.PatientDaoImpl;
import sae.semestre.six.email.EmailService;
import java.util.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/scheduling")
public class SchedulingController {

    AppointmentRepository appointmentRepository;

    DoctorDao doctorDao;

    PatientDao patientDao;

    @Autowired
    public SchedulingController(AppointmentRepository appointmentRepository, DoctorDao doctorDao, PatientDao patientDao) {
        this.appointmentRepository = appointmentRepository;
        this.doctorDao = doctorDao;
        this.patientDao = patientDao;
    }
    
    private final EmailService emailService = EmailService.getInstance();


    @PostMapping("/appointment")
    public String scheduleAppointment(
            @RequestParam Long doctorId,
            @RequestParam Long patientId,
            @RequestParam LocalDateTime appointmentDateTime) {
        try {
            Doctor doctor = doctorDao.findById(doctorId);

            // Retrieve all appointments for the doctor
            List<Appointment> doctorAppointments = appointmentRepository.findAllByDoctor_Id(doctorId);

            // Check for time conflict
            boolean conflict = doctorAppointments.stream().anyMatch(existing -> existing.getAppointmentDate().equals(appointmentDateTime));

            if (conflict) {
                return "Doctor is not available at this time";
            }

            // Check if the appointment is within working hours
            int hour = appointmentDateTime.getHour();
            if (hour < 9 || hour > 17) {
                return "Appointments only available between 9 AM and 5 PM";
            }

            // schedule appointment
            Appointment appointment = new Appointment();
            appointment.setDoctor(doctor);
            appointment.setAppointmentDate(appointmentDateTime);
            appointment.setPatient(patientDao.findById(patientId));
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



    @GetMapping("/available-slots")
    public List<LocalDateTime> getAvailableSlots(
            @RequestParam Long doctorId,
            @RequestParam LocalDate date) {

        List<LocalDateTime> availableSlots = new ArrayList<>();

        // Retrieve all appointments for the doctor
        List<Appointment> appointments = appointmentRepository.findAllByDoctor_Id(doctorId);

        // Iterate through hours from 9 AM to 5 PM
        for (int hour = 9; hour <= 17; hour++) {
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