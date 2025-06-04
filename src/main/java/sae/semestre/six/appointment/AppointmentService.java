/*
 * AppointmentService.java                                 22 mai 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sae.semestre.six.exception.EntityNotFoundException;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    @Autowired
    public AppointmentService(final AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
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
}
