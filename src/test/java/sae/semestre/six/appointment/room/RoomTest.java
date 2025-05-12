package sae.semestre.six.appointment.room;

import org.junit.jupiter.api.Test;
import sae.semestre.six.appointment.Appointment;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RoomTest {

    @Test
    void getId() {
        Room room = new Room();
        Long id = 1L;
        room.setId(id);
        assertEquals(id, room.getId());
    }

    @Test
    void setId() {
        Room room = new Room();
        Long id = 1L;
        room.setId(id);
        assertEquals(id, room.getId());
    }

    @Test
    void getRoomNumber() {
        Room room = new Room();
        String roomNumber = "A101";
        room.setRoomNumber(roomNumber);
        assertEquals(roomNumber, room.getRoomNumber());
    }

    @Test
    void setRoomNumber() {
        Room room = new Room();
        String roomNumber = "A101";
        room.setRoomNumber(roomNumber);
        assertEquals(roomNumber, room.getRoomNumber());
    }

    @Test
    void getFloor() {
        Room room = new Room();
        Integer floor = 1;
        room.setFloor(floor);
        assertEquals(floor, room.getFloor());
    }

    @Test
    void setFloor() {
        Room room = new Room();
        Integer floor = 1;
        room.setFloor(floor);
        assertEquals(floor, room.getFloor());
    }

    @Test
    void getType() {
        Room room = new Room();
        String type = "Consultation";
        room.setType(type);
        assertEquals(type, room.getType());
    }

    @Test
    void setType() {
        Room room = new Room();
        String type = "Consultation";
        room.setType(type);
        assertEquals(type, room.getType());
    }

    @Test
    void getCapacity() {
        Room room = new Room();
        Integer capacity = 5;
        room.setCapacity(capacity);
        assertEquals(capacity, room.getCapacity());
    }

    @Test
    void setCapacity() {
        Room room = new Room();
        Integer capacity = 5;
        room.setCapacity(capacity);
        assertEquals(capacity, room.getCapacity());
    }

    @Test
    void getIsOccupied() {
        Room room = new Room();
        Boolean isOccupied = true;
        room.setIsOccupied(isOccupied);
        assertEquals(isOccupied, room.getIsOccupied());
    }

    @Test
    void setIsOccupied() {
        Room room = new Room();
        Boolean isOccupied = true;
        room.setIsOccupied(isOccupied);
        assertEquals(isOccupied, room.getIsOccupied());
    }

    @Test
    void getAppointments() {
        Room room = new Room();
        Set<Appointment> appointments = new HashSet<>();
        room.setAppointments(appointments);
        assertEquals(appointments, room.getAppointments());
    }

    @Test
    void setAppointments() {
        Room room = new Room();
        Set<Appointment> appointments = new HashSet<>();
        room.setAppointments(appointments);
        assertEquals(appointments, room.getAppointments());
    }

    @Test
    void getAndSetCurrentPatientCount() {
        // Test 1: currentPatientCount < capacity => isOccupied should be false
        Room room = new Room();
        room.setCapacity(5);
        room.setCurrentPatientCount(3);
        assertEquals(3, room.getCurrentPatientCount());
        assertFalse(room.getIsOccupied());

        // Test 2: currentPatientCount = capacity => isOccupied should be true
        room.setCurrentPatientCount(5);
        assertEquals(5, room.getCurrentPatientCount());
        assertTrue(room.getIsOccupied());

        // Test 3: currentPatientCount > capacity => isOccupied should be true
        room.setCurrentPatientCount(6);
        assertEquals(6, room.getCurrentPatientCount());
        assertTrue(room.getIsOccupied());
    }

    @Test
    void canAcceptPatient() {
        Room room = new Room();

        // Test 1: Room has space and is not occupied
        room.setCapacity(5);
        room.setCurrentPatientCount(3);
        room.setIsOccupied(false);
        assertTrue(room.canAcceptPatient());

        // Test 2: Room has reached capacity
        room.setCurrentPatientCount(5);
        // isOccupied is automatically set to true when currentPatientCount >= capacity
        assertFalse(room.canAcceptPatient());

        // Test 3: Room is manually set to occupied despite having space
        room.setCurrentPatientCount(3);
        room.setIsOccupied(true);
        assertFalse(room.canAcceptPatient());
    }
}