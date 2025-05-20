/*
 * BillService.java                                  19 mai. 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.bill;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sae.semestre.six.appointment.doctor.Doctor;
import sae.semestre.six.appointment.medicalact.MedicalAct;
import sae.semestre.six.appointment.patient.Patient;

import java.util.List;

@Service
public class BillService {

    private final BillRepository billRepository;

    @Autowired
    public BillService(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    /**
     * Find all the bill that have the pending status
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
     * @param patient the patient associated with the bill
     * @param doctor the doctor associated with the bill
     * @param medicalActs the list of medical acts to be billed
     * @return the created and saved Bill
     * @throws IllegalArgumentException if the list of medical acts is empty or contains inactive acts
     */
    public Bill processBill(Patient patient, Doctor doctor, List<MedicalAct> medicalActs) throws IllegalArgumentException {
        Bill bill = new Bill();
        bill.setBillNumber("BILL" + System.currentTimeMillis());
        bill.setPatient(patient);
        bill.setDoctor(doctor);

        if (medicalActs.isEmpty()) {
            throw new IllegalArgumentException("No medical acts found");
        }
        if (!medicalActs.stream().allMatch(MedicalAct::isActive)) {
            throw new IllegalArgumentException("Some medical acts are inactive");
        }

        medicalActs.stream()
                .map(medicalAct -> {
                    BillDetail billDetail = new BillDetail();
                    billDetail.setMedicalAct(medicalAct);
                    billDetail.calculateLineTotal();
                    return billDetail;
                })
                .forEach(bill::addBillDetail);

        return billRepository.save(bill);
    }
}