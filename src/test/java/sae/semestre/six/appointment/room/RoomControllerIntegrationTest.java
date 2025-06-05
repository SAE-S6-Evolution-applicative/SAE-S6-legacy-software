package sae.semestre.six.appointment.room;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import sae.semestre.six.appointment.Appointment;
import sae.semestre.six.appointment.AppointmentRepository;
import sae.semestre.six.appointment.doctor.Doctor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@AutoConfigureMockMvc
class RoomControllerIntegrationTest {

    private MockMvc server;

    @MockitoBean
    private RoomRepository roomRepository;

    @MockitoBean
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private RoomController roomController;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        server = MockMvcBuilders.standaloneSetup(roomController).build();

        ReflectionTestUtils.setField(roomController, "roomRepository", roomRepository);
        ReflectionTestUtils.setField(roomController, "appointmentRepository", appointmentRepository);
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

        when(roomRepository.findByRoomNumber(room.getRoomNumber())).thenReturn(room);
        when(appointmentRepository.findById(appointment.getId())).thenReturn(Optional.of(appointment));

        server.perform(put("/rooms/" + room.getRoomNumber() + "/appointments/" + appointment.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Room assigned successfully"));

        verify(roomRepository, times(1)).save(any(Room.class));
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
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

        when(roomRepository.findByRoomNumber(room.getRoomNumber())).thenReturn(room);
        when(appointmentRepository.findById(appointment.getId())).thenReturn(Optional.of(appointment));

        server.perform(put("/rooms/" + room.getRoomNumber() + "/appointments/" + appointment.getId()))
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

        when(roomRepository.findByRoomNumber(room.getRoomNumber())).thenReturn(room);
        when(appointmentRepository.findById(appointment.getId())).thenReturn(Optional.of(appointment));

        server.perform(put("/rooms/" + room.getRoomNumber() + "/appointments/" + appointment.getId()))
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

        when(roomRepository.findByRoomNumber(room.getRoomNumber())).thenThrow(new RuntimeException("Room not found"));
        when(appointmentRepository.findById(appointment.getId())).thenReturn(Optional.of(appointment));

        server.perform(put("/rooms/" + room.getRoomNumber() + "/appointments/" + appointment.getId()))
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

        when(roomRepository.findByRoomNumber(room.getRoomNumber())).thenReturn(room);

        server.perform(get("/rooms/" + room.getRoomNumber() + "/availability"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.roomNumber").value(room.getRoomNumber()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.capacity").value(room.getCapacity()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currentPatients").value(room.getCurrentPatientCount()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available").value(true));
    }

    @Test
    void testGetRoomAvailabilityRoomNotFound() {
        String roomNumber = "A101";

        when(roomRepository.findByRoomNumber(roomNumber)).thenThrow(new RuntimeException("Room not found"));

        assertThrows(RuntimeException.class,
                () -> roomController.getRoomAvailability(roomNumber),
                "Should throw RuntimeException when room is not found");
    }
}