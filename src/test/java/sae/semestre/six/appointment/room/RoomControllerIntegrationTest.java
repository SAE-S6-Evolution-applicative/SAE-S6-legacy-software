package sae.semestre.six.appointment.room;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import sae.semestre.six.appointment.Appointment;
import sae.semestre.six.appointment.AppointmentDao;
import sae.semestre.six.appointment.doctor.Doctor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(RoomController.class)
class RoomControllerIntegrationTest {

    private MockMvc server;

    @MockitoBean
    private RoomDao roomDao;

    @MockitoBean
    private AppointmentDao appointmentDao;

    @InjectMocks
    private RoomController roomController;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        server = MockMvcBuilders.standaloneSetup(roomController).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testAssignWithoutError() throws Exception {
        Room room = new Room();
        room.setRoomNumber("A101");
        room.setCapacity(5);
        room.setCurrentPatientCount(0);
        room.setType("Consultation");

        Doctor generalDoctor = new Doctor();
        generalDoctor.setSpecialization("General");

        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setDoctor(generalDoctor);

        when(roomDao.findByRoomNumber(room.getRoomNumber())).thenReturn(room);
        when(appointmentDao.findById(appointment.getId())).thenReturn(appointment);

        server.perform(post("/rooms/assign")
                        .param("appointmentId", String.valueOf(appointment.getId()))
                        .param("roomNumber", room.getRoomNumber()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Room assigned successfully"));

        verify(roomDao, times(1)).update(any(Room.class));
        verify(appointmentDao, times(1)).update(any(Appointment.class));
    }

    @Test
    void testAssignSurgeonTypeDontMatch() throws Exception {
        Room room = new Room();
        room.setRoomNumber("A101");

        room.setType("SURGERY");


        Doctor generalDoctor = new Doctor();
        generalDoctor.setSpecialization("General");

        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setDoctor(generalDoctor);

        when(roomDao.findByRoomNumber(room.getRoomNumber())).thenReturn(room);
        when(appointmentDao.findById(appointment.getId())).thenReturn(appointment);

        server.perform(post("/rooms/assign")
                        .param("appointmentId", String.valueOf(appointment.getId()))
                        .param("roomNumber", room.getRoomNumber()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Error: Only surgeons can use surgery rooms"));
    }

    @Test
    void testAssignFullRoom() throws Exception {
        Room room = new Room();
        room.setRoomNumber("A101");
        room.setCapacity(5);
        room.setCurrentPatientCount(5);
        room.setType("Consultation");
        assertFalse(room.canAcceptPatient());
        Doctor generalDoctor = new Doctor();
        generalDoctor.setSpecialization("General");

        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setDoctor(generalDoctor);

        when(roomDao.findByRoomNumber(room.getRoomNumber())).thenReturn(room);
        when(appointmentDao.findById(appointment.getId())).thenReturn(appointment);

        server.perform(post("/rooms/assign")
                        .param("appointmentId", String.valueOf(appointment.getId()))
                        .param("roomNumber", room.getRoomNumber()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Error: Room is at full capacity"));
    }

    @Test
    void testAssignRoomNotFound() throws Exception {
        Room room = new Room();
        room.setRoomNumber("A101");
        room.setCapacity(5);
        room.setCurrentPatientCount(0);
        room.setType("Consultation");

        Doctor generalDoctor = new Doctor();
        generalDoctor.setSpecialization("General");

        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setDoctor(generalDoctor);

        when(roomDao.findByRoomNumber(room.getRoomNumber())).thenThrow(new RuntimeException("Room not found"));
        when(appointmentDao.findById(appointment.getId())).thenReturn(appointment);

        server.perform(post("/rooms/assign")
                        .param("appointmentId", String.valueOf(appointment.getId()))
                        .param("roomNumber", room.getRoomNumber()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Error: Room not found"));
    }

    @Test
    void testGetRoomAvailability() throws Exception {
        Room room = new Room();
        room.setRoomNumber("A101");
        room.setCapacity(5);
        room.setCurrentPatientCount(0);
        room.setType("Consultation");

        when(roomDao.findByRoomNumber(room.getRoomNumber())).thenReturn(room);

        server.perform(get("/rooms/availability")
                        .param("roomNumber", room.getRoomNumber()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.roomNumber").value(room.getRoomNumber()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.capacity").value(room.getCapacity()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currentPatients").value(room.getCurrentPatientCount()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available").value(true));
    }

    @Test
    void testGetRoomAvailabilityRoomNotFound() {
        String roomNumber = "A101";

        when(roomDao.findByRoomNumber(roomNumber)).thenThrow(new RuntimeException("Room not found"));

        assertThrows(RuntimeException.class,
                () -> roomController.getRoomAvailability(roomNumber),
                "Should throw RuntimeException when room is not found");
    }
}