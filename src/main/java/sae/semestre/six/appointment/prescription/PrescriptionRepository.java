/*
 * PrescriptionRepository.java                                  19 mai. 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.prescription;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findAllByPatient_Id(Long patientId);
} 