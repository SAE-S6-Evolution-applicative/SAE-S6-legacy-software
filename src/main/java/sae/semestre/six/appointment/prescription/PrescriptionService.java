/*
 * PrescriptionService.java                                 20 mai 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.prescription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sae.semestre.six.appointment.patient.Patient;
import sae.semestre.six.appointment.patient.PatientService;
import sae.semestre.six.exception.EntityNotFoundException;

import java.time.LocalDate;
import java.util.List;

@Service
public class PrescriptionService {

    private static final Logger logger = LoggerFactory.getLogger(PrescriptionService.class);

    private final MedicineService medicineService;

    private final PrescriptionRepository prescriptionRepository;

    private final PatientService patientService;

    @Autowired
    public PrescriptionService(
            final PrescriptionRepository prescriptionRepository,
            final PatientService patientService,
            final MedicineService medicineService
    ) {
        this.prescriptionRepository = prescriptionRepository;
        this.patientService = patientService;
        this.medicineService = medicineService;
    }

    /**
     * Adds a new prescription for a specified patient, including the list of medicines
     * and optional notes. The method validates the existence of the patient, retrieves the
     * most recent prescription for numbering purposes, and saves the prescription.
     * Additionally, it triggers the billing process associated with the created prescription.
     *
     * @param patientId the ID of the patient for whom the prescription is being created
     * @param medicineIds the list of medicine IDs to be included in the prescription
     * @param notes additional information or instructions related to the prescription
     */
    public void addPrescription(Long patientId, List<Long> medicineIds, String notes) {
        Patient patient = patientService.getPatient(patientId);
        Prescription lastPrescription = findLastPrescription();
        List<Medicine> medicineList = medicineService.getByIds(medicineIds);

        Prescription prescription = new Prescription(lastPrescription.extractNumericPartFromPrescriptionNumber(), patient, medicineList, notes);
        prescriptionRepository.save(prescription);

        logger.info("{} - {} \n", LocalDate.now(), prescription.getPrescriptionNumber());
    }

    /**
     * Retrieves the most recent prescription based on its creation date.
     * If no prescriptions are found, returns null.
     *
     * @return the most recently created Prescription object, or*/
    public Prescription findLastPrescription() {
        return prescriptionRepository.findLastPrescription().orElse(null);
    }

    /**
     * Retrieves all prescriptions associated with a specific patient ID.
     *
     * @param patientId the ID of the patient whose prescriptions are to be retrieved
     * @return a list of prescriptions associated with the specified patient ID
     */
    public List<Prescription> findAllPrescriptionsByPatientId(Long patientId) {
        return prescriptionRepository.findAllByPatient_Id(patientId);
    }

    /**
     * Retrieves a Prescription object based on the provided prescription ID.
     * If no prescription is found with the given ID, an IllegalArgumentException is thrown.
     *
     * @param prescriptionId the ID of the prescription to retrieve
     * @return the Prescription object associated with the given ID
     * @throws IllegalArgumentException if no prescription is found with the specified ID
     */
    public Prescription findPrescriptionById(Long prescriptionId) {
        return prescriptionRepository.findById(prescriptionId).orElseThrow(
                () -> new EntityNotFoundException("Prescription not found with ID : " + prescriptionId)
        );
    }

    /**
     * Calculates and returns the total cost of a prescription based on its ID.
     *
     * @param prescriptionId the ID of the prescription whose total cost is to be calculated
     * @return the total cost of the specified prescription
     * @throws IllegalArgumentException if no prescription is found with the specified ID
     */
    public double getTotalCost(Long prescriptionId) {
        Prescription prescription = findPrescriptionById(prescriptionId);
        return prescription.getTotalCost();
    }
}
