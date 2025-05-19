package sae.semestre.six.appointment.medicalrecord;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    MedicalRecord findByRecordNumber(String recordNumber);

    List<MedicalRecord> findAllByPatient_Id(Long patientId);

    List<MedicalRecord> findAllByDoctor_Id(Long doctorId);

    List<MedicalRecord> findAllByRecordDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<MedicalRecord> findAllByDiagnosisContainingIgnoreCase(String diagnosis);
}