/*
 * PrescriptionService.java                                 20 mai 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.prescription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sae.semestre.six.appointment.bill.BillController;
import sae.semestre.six.appointment.bill.BillService;
import sae.semestre.six.appointment.patient.Patient;
import sae.semestre.six.appointment.patient.PatientRepository;
import sae.semestre.six.appointment.patient.PatientService;

import java.io.FileWriter;
import java.time.LocalDate;
import java.util.ArrayList;
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
}
