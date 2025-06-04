/*
 * Appointment.java                                  19 mai. 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment;

import jakarta.persistence.*;
import sae.semestre.six.appointment.doctor.Doctor;
import sae.semestre.six.appointment.patient.Patient;
import sae.semestre.six.appointment.patient.history.PatientHistory;
import sae.semestre.six.appointment.room.Room;
import sae.semestre.six.exception.ScheduleAlreadyTakenException;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class Appointment {

    public static final int SCHEDULE_START_HOUR = 9;

    public static final int SCHEDULE_STOP_HOUR = 17;

    public Appointment() {}

    public Appointment(Doctor doctor, Patient patient, LocalDateTime date) {
        // Check if the appointment is within working hours
        if (date.getHour() < SCHEDULE_START_HOUR || date.getHour() > SCHEDULE_STOP_HOUR) {
            throw new ScheduleAlreadyTakenException("Appointments only available between 9 AM and 5 PM");
        }
        this.doctor = doctor;
        this.patient = patient;
        this.appointmentDate = date;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appointment_number", unique = true, nullable = false)
    private String appointmentNumber;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne
    private Room room;

    @ManyToOne
    private PatientHistory patientHistory;

    @Column(name = "appointment_date", nullable = false)
    private LocalDateTime appointmentDate;


    @Column(name = "status")
    private String status;

    @Column(name = "description")
    private String description;

    @Column(name = "room_number")
    private String roomNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppointmentNumber() {
        return appointmentNumber;
    }

    public void setAppointmentNumber(String appointmentNumber) {
        this.appointmentNumber = appointmentNumber;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public boolean hasTimeConflict(LocalDateTime appointmentDate) {
        return this.appointmentDate.equals(appointmentDate.withMinute(0).withSecond(0).withNano(0));
    }
} 