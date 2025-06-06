/*
 * PrescriptionRepository.java                                  19 mai. 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.prescription;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findAllByPatient_Id(Long patientId);

    @Query("SELECT p FROM Prescription p ORDER BY p.createdDate DESC limit 1")
    Optional<Prescription> findLastPrescription();
}