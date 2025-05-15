package sae.semestre.six.appointment.bill;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sae.semestre.six.appointment.medicalact.MedicalAct;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BillService {

    @Autowired
    public BillService(BillRepository billRepository, BillDetailRepository billDetailRepository) {
        this.billRepository = billRepository;
        this.billDetailRepository = billDetailRepository;
    }

    private BillDetailRepository billDetailRepository;

    private BillRepository billRepository;

    public void processBill(String patientId, String source, String[] items) {

    }

    public List<Bill> findPendingBills() {
        return billRepository.findBillsByStatus(Bill.Status.PENDING);
    }

    public Double getTotalRevenue() {
        return billRepository.findTotalRevenue();
    }
}