package sae.semestre.six.appointment.patient;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Patient findByPatientNumber(String patientNumber);
    List<Patient> findAllByLastName(String lastName);
} 