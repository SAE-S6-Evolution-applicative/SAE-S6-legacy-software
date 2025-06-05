/*
 * RoomControllerUnitTest.java                                 05 juin 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.room;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import sae.semestre.six.common.SuccessfullResponseModel;
import sae.semestre.six.exception.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoomControllerUnitTest {

    @Mock
    private RoomService roomService;

    @InjectMocks
    private RoomController roomController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAssignRoomSuccess() {
        Long appointmentId = 1L;
        String roomNumber = "A101";
        doNothing().when(roomService).assignRoom(appointmentId, roomNumber);

        SuccessfullResponseModel response = roomController.assignRoom(appointmentId, roomNumber);

        assertTrue(response.success());
        assertTrue(response.message().contains(roomNumber));
        verify(roomService, times(1)).assignRoom(appointmentId, roomNumber);
    }

    @Test
    void testAssignRoomThrowsException() {
        Long appointmentId = 2L;
        String roomNumber = "B404";
        doThrow(new EntityNotFoundException("Room not found with Room Number: B404")).when(roomService).assignRoom(appointmentId, roomNumber);

        assertThrows(RuntimeException.class, () -> roomController.assignRoom(appointmentId, roomNumber));
        verify(roomService, times(1)).assignRoom(appointmentId, roomNumber);
    }

    @Test
    void testGetRoomAvailabilitySuccess() {
        Room room = new Room();
        room.setRoomNumber("A101");
        room.setCapacity(5);
        room.setCurrentPatientCount(2);

        when(roomService.findByRoomNumber("A101")).thenReturn(room);

        RoomController.RoomAvailabilityResponse response = roomController.getRoomAvailability("A101");

        assertEquals("A101", response.roomNumber());
        assertEquals(5, response.capacity());
        assertEquals(2, response.currentPatients());
        assertEquals(room.canAcceptPatient(), response.available());
    }

    @Test
    void testGetRoomAvailabilityRoomNotFound() {
        when(roomService.findByRoomNumber("B404")).thenThrow(new EntityNotFoundException("Room not found with Room Number: B404"));

        assertThrows(RuntimeException.class, () -> roomController.getRoomAvailability("B404"));
    }
}
