/*
 * BillService.java                                  19 mai. 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.bill;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sae.semestre.six.FileHandler;
import sae.semestre.six.HashUtils;
import sae.semestre.six.appointment.doctor.Doctor;
import sae.semestre.six.appointment.medicalact.MedicalAct;
import sae.semestre.six.appointment.patient.Patient;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class BillService {

    private final BillRepository billRepository;
    private final FileHandler fileHandler;

    @Value("${app.security.hash-folder-path}")
    private String parentHashFileFolder;

    @Autowired
    public BillService(BillRepository billRepository, FileHandler fileHandler) {
        this.billRepository = billRepository;
        this.fileHandler = fileHandler;
    }

    /**
     * Find all the bill that have the pending status
     *
     * @return all the pending Bill
     */
    public List<Bill> findPendingBills() {
        return billRepository.findBillsByStatus(Bill.Status.PENDING);
    }

    public Double getTotalRevenue() {
        Double totalRevenue = billRepository.findTotalRevenue();
        if (totalRevenue == null) {
            totalRevenue = 0.0;
        }
        return totalRevenue;
    }

    /**
     * Creates a bill for a patient, a doctor, and a list of medical acts.
     *
     * @param patient     the patient associated with the bill
     * @param doctor      the doctor associated with the bill
     * @param medicalActs the list of medical acts to be billed
     * @return the created and saved Bill
     * @throws IllegalArgumentException if the list of medical acts is empty or contains inactive acts
     */
    public Bill processBill(Patient patient, Doctor doctor, List<MedicalAct> medicalActs) throws IllegalArgumentException {
        Bill bill = new Bill(patient, doctor, medicalActs);

        String hashed = HashUtils.hashString(bill.getInfoToHash());
        Path path = getFileName(bill);
        fileHandler.writeHashToFile(hashed, path.toUri().getPath());
        bill.setHash(hashed);

        return billRepository.save(bill);
    }

    private Path getFileName(Bill bill) {
        return Path.of(parentHashFileFolder + File.separator + bill.getBillNumber());
    }

    public boolean verify(Bill bill) {
        boolean billValid = false;
        Path path = getFileName(bill);

        if (Files.exists(path)) {
            String content = fileHandler.readFile(path);
            billValid = content.equals(HashUtils.hashString(bill.getInfoToHash()));
        }

        return billValid;
    }

    public Bill getBillByNumber(String number) {
    	return billRepository.findBillByBillNumber(number);
    }
}