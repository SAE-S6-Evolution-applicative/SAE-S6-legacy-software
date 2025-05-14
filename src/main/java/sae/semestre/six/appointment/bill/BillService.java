package sae.semestre.six.appointment.bill;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BillService {

    @Autowired
    public BillService(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    private BillRepository billRepository;

    public void processBill(String patientId, String source, String[] items) {
        
    }

    public List<Bill> findPendingBills() {
        return billRepository.findBillsByStatus(Bill.Status.PENDING.name());
    }
} 