package sae.semestre.six.appointment.medicalrecord;

import sae.semestre.six.generic.GenericDao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MedicalRecordDao extends GenericDao<MedicalRecord, Long> {
    MedicalRecord findByRecordNumber(String recordNumber);
    List<MedicalRecord> findByPatientId(Long patientId);
    List<MedicalRecord> findByDoctorId(Long doctorId);
    List<MedicalRecord> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<MedicalRecord> findByDiagnosis(String diagnosis);
}