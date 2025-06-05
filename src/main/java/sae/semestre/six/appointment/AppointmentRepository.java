/*
 * AppointmentRepository.java                                  19 mai. 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findAllByPatient_Id(Long patientId);

    List<Appointment> findAllByDoctor_Id(Long doctorId);

    List<Appointment> findAllByAppointmentDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Appointment> findAllByDoctor_IdAndAppointmentDateBetween(Long doctorId, LocalDateTime startDate, LocalDateTime endDate);
} 