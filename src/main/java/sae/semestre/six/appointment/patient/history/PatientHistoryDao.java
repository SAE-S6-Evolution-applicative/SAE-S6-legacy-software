package sae.semestre.six.appointment.patient.history;

import sae.semestre.six.generic.GenericDao;

import java.time.LocalDateTime;
import java.util.List;
import java.time.LocalDate;

public interface PatientHistoryDao extends GenericDao<PatientHistory, Long> {
    List<PatientHistory> findCompleteHistoryByPatientId(Long patientId);
    List<PatientHistory> searchByMultipleCriteria(String keyword, LocalDateTime startDate, LocalDateTime endDate);
} 