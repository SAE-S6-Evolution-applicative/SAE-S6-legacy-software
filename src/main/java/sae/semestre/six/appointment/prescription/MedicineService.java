/*
 * MedicineService.java                                 20 mai 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.prescription;

import org.springframework.stereotype.Service;
import sae.semestre.six.exception.EntityNotFoundException;

import java.util.List;

@Service
public class MedicineService {

    private final MedicineRepository medicineRepository;

    MedicineService(final MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    /**
     * Retrieves a medicine entity based on its unique identifier.
     *
     * @param id the unique identifier of the medicine to retrieve
     * @return the medicine associated with the given identifier
     * @throws IllegalArgumentException if no medicine is found with the provided identifier
     */
    public Medicine getMedicineById(Long id) {
        return medicineRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("No medicine found with ID: " + id)
        );
    }

    /**
     * Retrieves all medicines from the repository.
     *
     * @return a list of all available medicines
     */
    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }

    /**
     * Retrieves all medicines with the given IDs
     *
     * @param ids List of medicine IDs to retrieve
     * @return List of found medicines (may not contain all requested IDs if some don't exist)
     */
    public List<Medicine> getByIds(List<Long> ids) {
        return medicineRepository.findAllById(ids);
    }
}
