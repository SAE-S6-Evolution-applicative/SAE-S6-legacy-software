package sae.semestre.six.appointment.patient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    @Autowired
    PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public Patient getPatient(Long patientId) {
        return patientRepository.findById(patientId).orElseThrow(
                () -> new RuntimeException("Patient not found")
        );
    }
}
