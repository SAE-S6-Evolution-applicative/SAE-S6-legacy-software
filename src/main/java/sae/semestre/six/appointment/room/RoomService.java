/*
 * RoomService.java                                 22 mai 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.room;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sae.semestre.six.appointment.Appointment;
import sae.semestre.six.appointment.AppointmentRepository;
import sae.semestre.six.appointment.AppointmentService;
import sae.semestre.six.exception.EntityNotFoundException;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    private final AppointmentService appointmentService;

    private final AppointmentRepository appointmentRepository;

    @Autowired
    public RoomService(final RoomRepository roomRepository,
                       final AppointmentService appointmentService,
                       final AppointmentRepository appointmentRepository) {
        this.roomRepository = roomRepository;
        this.appointmentService = appointmentService;
        this.appointmentRepository = appointmentRepository;
    }

    /**
     * Finds a room by its room number.
     *
     * @param roomNumber the room number to search for, represented as a string
     * @return the Room entity associated with the given room number
     * @throws EntityNotFoundException if no room is found with the specified room number
     */
    public Room findByRoomNumber(String roomNumber) {
        return roomRepository.findByRoomNumber(roomNumber).orElseThrow(
                () -> new EntityNotFoundException("Room not found with Room Number: " + roomNumber)
        );
    }

    /**
     * Assigns a room to a given appointment.
     *
     * This method checks the compatibility between the room and the appointment,
     * updates the number of patients in the room, and associates the room number with the appointment.
     *
     * @param appointmentId the ID of the appointment to assign the room to
     * @param roomNumber the number of the room to assign
     * @throws EntityNotFoundException if the room or appointment does not exist
     * @throws IllegalArgumentException if the room cannot accept this appointment
     */
    public void assignRoom(Long appointmentId, String roomNumber) {
        Room room = this.findByRoomNumber(roomNumber);
        Appointment appointment = appointmentService.findAppointmentById(appointmentId);

        // Core logic in the entity, will throw an Exception if invalid.
        room.canAssignTo(appointment);
        room.assignPatient();
        appointment.setRoomNumber(roomNumber);

        roomRepository.save(room);
        appointmentRepository.save(appointment);
    }
}
