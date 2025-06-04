/*
 * AppointmentService.java                                 22 mai 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sae.semestre.six.appointment.AppointmentController.AvailableSlotRequestModel;
import sae.semestre.six.appointment.AppointmentController.ScheduleRequestModel;
import sae.semestre.six.appointment.doctor.Doctor;
import sae.semestre.six.appointment.doctor.DoctorService;
import sae.semestre.six.appointment.patient.Patient;
import sae.semestre.six.appointment.patient.PatientService;
import sae.semestre.six.appointment.prescription.PrescriptionService;
import sae.semestre.six.email.EmailService;
import sae.semestre.six.exception.EntityNotFoundException;
import sae.semestre.six.exception.ScheduleAlreadyTakenException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static sae.semestre.six.appointment.Appointment.SCHEDULE_START_HOUR;
import static sae.semestre.six.appointment.Appointment.SCHEDULE_STOP_HOUR;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    private final DoctorService doctorService;

    private final PatientService patientService;

    private final EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(PrescriptionService.class);

    @Autowired
    public AppointmentService(
            final AppointmentRepository appointmentRepository,
            final DoctorService doctorService,
            final PatientService patientService,
            final EmailService emailService) {
        this.appointmentRepository = appointmentRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.emailService = emailService;
    }

    /**
     * Retrieves an appointment by its unique identifier.
     * If no appointment is found with the given ID, an {@code EntityNotFoundException} is thrown.
     *
     * @param appointmentId the unique identifier of the appointment to be retrieved
     * @return the {@code Appointment} object corresponding to the given ID
     * @throws EntityNotFoundException if no appointment is found with the specified ID
     */
    public Appointment findAppointmentById(Long appointmentId) {
        return appointmentRepository.findById(appointmentId).orElseThrow(
                () -> new EntityNotFoundException("No Appointment found with ID : " + appointmentId)
        );
    }

    /**
     * Schedules a new appointment for a doctor and a patient at a specified date and time.
     * <p>
     * This method checks for scheduling conflicts for the doctor at the requested time.
     * If a conflict exists, a {@link ScheduleAlreadyTakenException} is thrown.
     * If the appointment is successfully scheduled, an email notification is sent to the doctor.
     *
     * @param scheduleRequestModel the request model containing doctor ID, patient ID, and appointment date/time
     * @return {@code true} if the appointment was successfully scheduled
     * @throws ScheduleAlreadyTakenException if the doctor already has an appointment at the requested time
     * @throws EntityNotFoundException if the doctor or patient does not exist
     */
    public boolean scheduleAppointment(ScheduleRequestModel scheduleRequestModel) {
        Doctor doctor = doctorService.getDoctor(scheduleRequestModel.doctorId());
        Patient patient = patientService.getPatient(scheduleRequestModel.patientId());

        //Should be round hours because the former logic was using only hour
        LocalDateTime appointmentTime = scheduleRequestModel.appointmentDateTime().withMinute(0).withSecond(0).withNano(0);

        // Retrieve all appointments for the doctor
        List<Appointment> doctorAppointments = appointmentRepository.findAllByDoctor_Id(scheduleRequestModel.doctorId());

        if (isDoctorTimeConflict(doctorAppointments, appointmentTime)) {
            logger.info("{} - Doctor with Id {} already exists\n", LocalDate.now(), doctor.getId());
            throw new ScheduleAlreadyTakenException("Doctor is not available at this time");
        }

        // schedule appointment
        Appointment appointment = new Appointment(doctor, patient, appointmentTime);
        appointmentRepository.save(appointment);
        logger.info("{} - Appointment {} created\n", LocalDate.now(), appointment.getId());

        // Send email notification
        emailService.sendEmail(
                doctor.getEmail(),
                "New Appointment Scheduled",
                "You have a new appointment on " + scheduleRequestModel.appointmentDateTime()
        );

        return true;
    }

    /**
     * Checks if there is a scheduling conflict for the doctor at the given appointment date and time.
     *
     * @param doctorAppointments the list of existing appointments for the doctor
     * @param appointmentDateTime the date and time to check for a conflict
     * @return true if there is a conflict (the doctor already has an appointment at this time), false otherwise
     */
    private boolean isDoctorTimeConflict(List<Appointment> doctorAppointments, LocalDateTime appointmentDateTime) {
        return doctorAppointments.stream()
                .anyMatch(a -> a.hasTimeConflict(appointmentDateTime));
    }


    /**
     * Retrieves the list of available appointment time slots for a given doctor on a specific date.
     * <p>
     * This method generates all possible hourly slots within the doctor's working hours (from {@link Appointment#SCHEDULE_START_HOUR}
     * to {@link Appointment#SCHEDULE_START_HOUR}) for the specified date, then removes any slots that are already taken by existing appointments.
     *
     * @param availableSlotRequestModel the request model containing the doctor ID and the date for which to find available slots
     * @return a list of available {@link LocalDateTime} slots for the doctor on the requested date
     */
    public List<LocalDateTime> getAvailableTimeSlots(AvailableSlotRequestModel availableSlotRequestModel) {
        LocalDate requestedDate = availableSlotRequestModel.date();
        Long doctorId = availableSlotRequestModel.doctorId();

        // Get appointments for the specific doctor on the requested date only
        List<Appointment> dailyAppointments = appointmentRepository
                .findAllByDoctor_IdAndAppointmentDateBetween(
                        doctorId,
                        requestedDate.atStartOfDay(),
                        requestedDate.atTime(23, 59, 59)
                );

        // create a list with all daily slots within working hours
        List<LocalDateTime> finalSlots = new ArrayList<>();
        for (int hour = SCHEDULE_START_HOUR; hour <= SCHEDULE_STOP_HOUR; hour++) {
            finalSlots.add(LocalDateTime.of(requestedDate, LocalTime.of(hour, 0)));
        }

        //Remove slots already taken
        for (Appointment appointment : dailyAppointments) {
            //Should be round hours because the former logic was using only hour
            finalSlots.remove(appointment.getAppointmentDate().withMinute(0).withSecond(0).withNano(0));
        }

        return finalSlots;
    }
}
