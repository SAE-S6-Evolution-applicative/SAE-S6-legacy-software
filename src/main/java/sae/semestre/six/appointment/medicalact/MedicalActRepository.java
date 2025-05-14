package sae.semestre.six.appointment.medicalact;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface MedicalActRepository extends JpaRepository<MedicalAct, Long> {

}
