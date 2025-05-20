/*
 * BillDetailService.java                                  19 mai. 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.bill;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sae.semestre.six.appointment.medicalact.MedicalAct;

import java.util.List;

@Service
public class BillDetailService {

    private final BillDetailRepository billDetailRepository;

    @Autowired
    public BillDetailService(final BillDetailRepository billDetailRepository) {
        this.billDetailRepository = billDetailRepository;
    }

    /**
     * Update all the bill detail that mention the old MedicalAct with the new one.
     *
     * All the BillDetail that mention the old MedicalAct and their Bill is in Pending status are fetch updated adn return
     *
     * @param oldMedicalAct The MedicalAct prior to is update
     * @param newMedicalAct The MedicalAct after is update
     * @return All the BillDetail updated
     */
    public List<BillDetail> updateBillDetail(MedicalAct oldMedicalAct, MedicalAct newMedicalAct) {
        List<BillDetail> billDetails = billDetailRepository.findAllByBill_StatusAndMedicalAct(Bill.Status.PENDING, oldMedicalAct);

        List<BillDetail> updatedBillDetails = billDetails.stream()
                .peek(billDetail -> billDetail.setMedicalAct(newMedicalAct))
                .toList();

        return billDetailRepository.saveAll(updatedBillDetails);
    }
}
