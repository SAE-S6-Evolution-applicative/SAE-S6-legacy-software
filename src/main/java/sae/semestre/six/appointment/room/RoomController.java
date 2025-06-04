/*
 * RoomController.java                                  19 mai. 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.room;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sae.semestre.six.appointment.Appointment;
import sae.semestre.six.appointment.AppointmentRepository;
import sae.semestre.six.common.SuccessfullResponseModel;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/rooms")
@Tag(name = "Rooms", description = "Room management API")
public class RoomController {

    private final RoomService roomService;

    @Autowired
    public RoomController(final RoomService roomService) {
        this.roomService = roomService;
    }

    @Operation(summary = "Assign a room", description = "Assigns a room to an appointment")
    @ApiResponse(responseCode = "200", description = "Room assigned successfully")
    @ApiResponse(responseCode = "400", description = "Invalid data or room unavailable")
    @PutMapping("/{roomNumber}/appointments/{appointmentId}")
    public SuccessfullResponseModel assignRoom(
            @Parameter(description = "Appointment ID") @PathVariable Long appointmentId,
            @Parameter(description = "Room number") @PathVariable String roomNumber) {
        roomService.assignRoom(appointmentId, roomNumber);
        return new SuccessfullResponseModel("Correctly assigned room %s".formatted(roomNumber), true);
    }
    
    @Operation(summary = "Check room availability", description = "Retrieves room availability information")
    @ApiResponse(responseCode = "200", description = "Availability information")
    @GetMapping("/{roomNumber}/availability")
    public RoomAvailabilityResponse getRoomAvailability(
            @Parameter(description = "Room number") @PathVariable String roomNumber) {
        Room room = roomService.findByRoomNumber(roomNumber);
        return new RoomAvailabilityResponse(room.getRoomNumber(), room.getCapacity(), room.getCurrentPatientCount(), room.canAcceptPatient());
    }

    /**
     * Response model for room availability.
     *
     * @param roomNumber the unique identifier of the room
     * @param capacity the maximum number of patients the room can accommodate
     * @param currentPatients the current number of patients in the room
     * @param available true if the room can accept new patients, false otherwise
     */
    record RoomAvailabilityResponse(String roomNumber,
                                    Integer capacity,
                                    Integer currentPatients,
                                    Boolean available) {}
} 