package sae.semestre.six.appointment.bill;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sae.semestre.six.appointment.doctor.Doctor;
import sae.semestre.six.appointment.medicalact.MedicalAct;
import sae.semestre.six.appointment.patient.Patient;

import java.util.List;

@Service
public class BillService {

    @Autowired
    public BillService(BillRepository billRepository, BillDetailRepository billDetailRepository) {
        this.billRepository = billRepository;
        this.billDetailRepository = billDetailRepository;
    }

    private final BillDetailRepository billDetailRepository;

    private final BillRepository billRepository;


    public List<Bill> findPendingBills() {
        return billRepository.findBillsByStatus(Bill.Status.PENDING);
    }

    public void processBill(String patientId, String source, String[] items) {

    }

    public Double getTotalRevenue() {
        return billRepository.findTotalRevenue();
    }

    public Bill processBill(Patient patient, Doctor doctor, List<MedicalAct> medicalActs) throws Exception {
        Bill bill = new Bill();
        bill.setBillNumber("BILL" + System.currentTimeMillis());
        bill.setPatient(patient);
        bill.setDoctor(doctor);

        if (medicalActs.isEmpty()) {
            throw new Exception("No medical acts found");
        }
        if (!medicalActs.stream().allMatch(MedicalAct::isActive)) {
            throw new Exception("Some medical acts are inactive");
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