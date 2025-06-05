/*
 * MedicineRepository.java                                 20 mai 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.prescription;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {
}
