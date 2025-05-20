/*
 * DoctorRepository.java                                  19 mai. 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.doctor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Doctor findByDoctorNumber(String doctorNumber);

    List<Doctor> findAllBySpecialization(String specialization);

    List<Doctor> findAllByDepartment(String department);
} 