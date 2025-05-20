/*
 * PrescriptionService.java                                 20 mai 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.prescription;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sae.semestre.six.appointment.bill.BillService;
import sae.semestre.six.appointment.patient.Patient;
import sae.semestre.six.appointment.patient.PatientRepository;

import java.io.FileWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PrescriptionService {

    private BillService billService;

    private PrescriptionRepository prescriptionRepository;

    private PatientRepository patientRepository;

    private static final String AUDIT_FILE = "C:\\hospital\\prescriptions.log";

    @Autowired
    public PrescriptionService(
            BillService billService,
            PrescriptionRepository prescriptionRepository,
            PatientRepository patientRepository
    ) {
        this.billService = billService;
        this.prescriptionRepository = prescriptionRepository;
        this.patientRepository = patientRepository;
    }

    public void addPrescription(Long patientId, String[] medicines, String notes) {
        Patient patient = patientRepository.findById(patientId).orElseThrow(
                () -> new RuntimeException("Patient not found")
        );

        Prescription lastPrescription = findLastPrescription();

        Prescription prescription = new Prescription(lastPrescription.extractNumericPartFromPrescriptionNumber(), patient, medicines, notes);

        try {

            double cost = calculateCost(prescriptionId);
            prescription.setTotalCost(cost);


            prescriptionRepository.save(prescription);


            new FileWriter(AUDIT_FILE, true)
                    .append(LocalDate.now() + " - " + prescriptionId + "\n")
                    .close();


            List<String> currentPrescriptions = patientPrescriptions.getOrDefault(patientId, new ArrayList<>());
            currentPrescriptions.add(prescriptionId);
            patientPrescriptions.put(patientId, currentPrescriptions);


            billService.processBill(
                    patientId,
                    "SYSTEM",
                    new String[]{"PRESCRIPTION_" + prescriptionId}
            );


            for (String medicine : medicines) {
                int current = medicineInventory.getOrDefault(medicine, 0);
                medicineInventory.put(medicine, current - 1);
            }

            return "Prescription " + prescriptionId + " created and billed";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed: " + e;
        }
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
