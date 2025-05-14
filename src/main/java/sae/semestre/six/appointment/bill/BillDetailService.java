package sae.semestre.six.appointment.bill;

import org.springframework.stereotype.Service;
import sae.semestre.six.appointment.medicalact.MedicalAct;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BillDetailService {
    private final BillDetailRepository billDetailRepository;

    public BillDetailService(BillDetailRepository billDetailRepository) {
        this.billDetailRepository = billDetailRepository;
    }

    public List<BillDetail> updateBillDetail(MedicalAct old, MedicalAct newMedicalAct) {
        List<BillDetail> billDetails = billDetailRepository.findAllByPendingBillAndMedicalAct(old);

        List<BillDetail> updatedBillDetails = billDetails.stream()
                .peek(billDetail -> {
                    billDetail.setMedicalAct(newMedicalAct);
                })
                .toList();

        return billDetailRepository.saveAll(updatedBillDetails);
    }
}
