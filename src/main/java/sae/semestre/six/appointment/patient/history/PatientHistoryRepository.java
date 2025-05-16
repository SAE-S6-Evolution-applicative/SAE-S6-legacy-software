package sae.semestre.six.appointment.patient.history;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PatientHistoryRepository extends JpaRepository<PatientHistory, Long>, JpaSpecificationExecutor<PatientHistory> {
    List<PatientHistory> findAllByPatient_Id(Long patientId);
} 