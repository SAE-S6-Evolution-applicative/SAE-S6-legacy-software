/*
 * MedicalActRepository.java                                  19 mai. 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.medicalact;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalActRepository extends JpaRepository<MedicalAct, Long> {

    List<MedicalAct> findAllByActive(boolean active);
}
