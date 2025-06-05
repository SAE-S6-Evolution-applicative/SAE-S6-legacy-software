/*
 * RoomControllerIntegrationTest.java                              19 mai. 2025
 * IUT de Rodez, no author rights
 */


package sae.semestre.six.appointment.room;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import sae.semestre.six.appointment.Appointment;
import sae.semestre.six.appointment.AppointmentRepository;
import sae.semestre.six.appointment.doctor.Doctor;
import sae.semestre.six.appointment.doctor.DoctorRepository;
import sae.semestre.six.appointment.patient.Patient;
import sae.semestre.six.appointment.patient.PatientRepository;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@AutoConfigureMockMvc
class RoomControllerIntegrationTest {

    @Autowired
    private MockMvc server;

    @Autowired
    private RoomService roomService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    private Room room;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        // Create an available room
        room = new Room();
        room.setRoomNumber("A101");
        room.setCapacity(2);
        room.setCurrentPatientCount(0);
        room.setType("Consultation");
        roomRepository.save(room);

        // Create a doctor and a patient
        Doctor doctor = new Doctor();
        doctor.setFirstName("Will");
        doctor.setLastName("Smith");
        doctor.setSpecialization("General Practitioner");
        doctor.setDoctorNumber("DOC001");
        doctorRepository.save(doctor);

        Patient patient = new Patient();
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setPatientNumber("PAT001");
        patientRepository.save(patient);

        // Create an appointment
        appointment = new Appointment(doctor, patient, LocalDateTime.now());
        appointment.setAppointmentNumber("APP001");
        appointmentRepository.save(appointment);
    }

    @Test
    void testAssignRoomSuccess() throws Exception {
        server.perform(put("/rooms/A101/appointments/" + appointment.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("A101")));
    }

    @Test
    void testAssignRoomNotFound() throws Exception {
        server.perform(put("/rooms/B404/appointments/" + appointment.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testAssignFullRoom() throws Exception {
        room.setCurrentPatientCount(room.getCapacity());
        roomRepository.save(room);

        server.perform(put("/rooms/A101/appointments/" + appointment.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }
}