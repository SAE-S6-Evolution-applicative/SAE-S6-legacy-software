package sae.semestre.six.appointment.room;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sae.semestre.six.appointment.Appointment;
import sae.semestre.six.appointment.AppointmentRepository;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/rooms")
@Tag(name = "Rooms", description = "Room management API")
public class RoomController {

    private final RoomRepository roomRepository;
    
    private final AppointmentRepository appointmentRepository;

    @Autowired
    public RoomController(AppointmentRepository appointmentRepository, RoomRepository roomRepository) {
        this.appointmentRepository = appointmentRepository;
        this.roomRepository = roomRepository;
    }


    @Operation(summary = "Assign a room", description = "Assigns a room to an appointment")
    @ApiResponse(responseCode = "200", description = "Room assigned successfully")
    @ApiResponse(responseCode = "400", description = "Invalid data or room unavailable")
    @PostMapping("/assign")
    public String assignRoom(
            @Parameter(description = "Appointment ID") @RequestParam Long appointmentId,
            @Parameter(description = "Room number") @RequestParam String roomNumber) {
        try {
            Room room = roomRepository.findByRoomNumber(roomNumber);
            Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(
                    () -> new Exception()
            );
            
            
            if (room.getType().equals("SURGERY") && 
                !appointment.getDoctor().getSpecialization().equals("SURGEON")) {
                return "Error: Only surgeons can use surgery rooms";
            }
            
            
            if (room.getCurrentPatientCount() >= room.getCapacity()) {
                return "Error: Room is at full capacity";
            }
            
            
            room.setCurrentPatientCount(room.getCurrentPatientCount() + 1);
            appointment.setRoomNumber(roomNumber);
            
            roomRepository.save(room);
            appointmentRepository.save(appointment);
            
            return "Room assigned successfully";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    
    @Operation(summary = "Check room availability", description = "Retrieves room availability information")
    @ApiResponse(responseCode = "200", description = "Availability information")
    @GetMapping("/availability")
    public Map<String, Object> getRoomAvailability(
            @Parameter(description = "Room number") @RequestParam String roomNumber) {
        Room room = roomRepository.findByRoomNumber(roomNumber);
        Map<String, Object> result = new HashMap<>();
        
        result.put("roomNumber", room.getRoomNumber());
        result.put("capacity", room.getCapacity());
        result.put("currentPatients", room.getCurrentPatientCount());
        result.put("available", room.canAcceptPatient());
        
        return result;
    }
} 