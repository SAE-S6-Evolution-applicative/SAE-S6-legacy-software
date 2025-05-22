/*
 * PatientService.java                                  19 mai. 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.patient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sae.semestre.six.exception.EntityNotFoundException;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    @Autowired
    PatientService(final PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    /**
     * Retrieves a patient entity from the repository based on their unique identifier.
     *
     * @param patientId the unique identifier of the patient to retrieve
     * @return the patient entity associated with the given ID
     * @throws IllegalArgumentException if no patient is found with the specified ID
     */
    public Patient getPatient(Long patientId) {
        return patientRepository.findById(patientId).orElseThrow(
                () -> new EntityNotFoundException("Patient not found with ID : " + patientId)
        );
    }
}
