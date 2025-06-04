/*
 * Room.java                                  19 mai. 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.room;

import jakarta.persistence.*;
import sae.semestre.six.appointment.Appointment;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_number", unique = true)
    private String roomNumber;

    @Column(name = "floor")
    private Integer floor;

    @Column(name = "type")
    private String type;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "is_occupied")
    private Boolean isOccupied = false;

    @OneToMany(mappedBy = "room")
    private Set<Appointment> appointments = new HashSet<>();


    @Column(name = "current_patient_count")
    private Integer currentPatientCount = 0;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Boolean getIsOccupied() {
        return isOccupied;
    }

    public void setIsOccupied(Boolean isOccupied) {
        this.isOccupied = isOccupied;
    }

    public Set<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(Set<Appointment> appointments) {
        this.appointments = appointments;
    }

    public Integer getCurrentPatientCount() {
        return currentPatientCount;
    }

    public void setCurrentPatientCount(Integer currentPatientCount) {
        this.currentPatientCount = currentPatientCount;

        this.isOccupied = currentPatientCount >= capacity;
    }

    /**
     * Determines whether the current room can accept a new patient.
     *
     * This method checks if the room's current patient count is below its
     * capacity and if the room is not marked as occupied.
     *
     * @return true if the room can accept a new patient, false otherwise
     */
    public boolean canAcceptPatient() {
        return currentPatientCount < capacity && !isOccupied;
    }

    /**
     * Determines if the appointment can be assigned to the current room.
     *
     * Validates specific conditions such as the room type and specialization
     * of the doctor assigned to the appointment. For example, surgery
     * rooms require surgeons. Additionally, checks if the room has
     * available capacity before assigning the appointment.
     *
     * @param appointment the appointment to be assigned to the room
     * @return true if the appointment can be assigned to the room
     * @throws IllegalArgumentException if the room type and doctor's specialization are incompatible
     *         or if the room has reached its full capacity
     */
    public boolean canAssignTo(Appointment appointment) {
        if ("SURGERY".equals(this.type) &&
                !"SURGEON".equals(appointment.getDoctor().getSpecialization())) {
            throw new IllegalArgumentException("Error: Only surgeons can use surgery rooms");
        }
        if (this.currentPatientCount >= this.capacity) {
            throw new IllegalArgumentException("Error: Room is at full capacity");
        }
        return true;
    }

    /**
     * Assigns a patient to the room.
     *
     * This method increments the current patient count of the room and
     * updates the occupancy status based on whether the room has reached
     * its capacity. If the updated patient count matches or exceeds the
     * room's capacity, the room is marked as occupied.
     */
    public void assignPatient() {
        this.currentPatientCount++;
        this.isOccupied = currentPatientCount >= capacity;
    }
} 