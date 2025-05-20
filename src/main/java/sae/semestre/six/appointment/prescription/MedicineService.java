/*
 * MedicineService.java                                 20 mai 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.prescription;

import org.springframework.stereotype.Service;

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
                () -> new IllegalArgumentException("No medicine found with ID: " + id)
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
}
