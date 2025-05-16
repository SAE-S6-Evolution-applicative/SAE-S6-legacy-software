package sae.semestre.six.appointment.medicalact;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalActRepository extends JpaRepository<MedicalAct, Long> {

    List<MedicalAct> findAllByActive(boolean active);
}
