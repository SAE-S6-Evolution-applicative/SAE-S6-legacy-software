package sae.semestre.six.appointment.bill;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;
import sae.semestre.six.appointment.medicalact.MedicalAct;
import sae.semestre.six.appointment.medicalact.MedicalActRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Transactional
public class BillDetailServiceTest {

    @Autowired
    private BillDetailService billDetailService;
    @Autowired
    private MedicalActRepository medicalActRepository;
    @Autowired
    private BillRepository billRepository;
    @MockitoSpyBean
    private BillDetailRepository billDetailRepository;

    @Test
    void testUpdateBillDetail() {
        // Arrange
        MedicalAct oldMedicalAct = medicalActRepository.save(new MedicalAct("Consultation", 50.0));

        Bill pendingWithMedical = new Bill();
        pendingWithMedical.setStatus(Bill.Status.PENDING);
        billRepository.save(pendingWithMedical);

        BillDetail withMedicalAct1 = new BillDetail();
        withMedicalAct1.setMedicalAct(oldMedicalAct);
        withMedicalAct1.setBill(pendingWithMedical);
        billDetailRepository.save(withMedicalAct1);

        // Save additional bills for completeness
        Bill pending = new Bill();
        pending.setStatus(Bill.Status.PENDING);
        billRepository.save(pending);

        Bill pay = new Bill();
        pay.setStatus(Bill.Status.PAID);
        billRepository.save(pay);

        BillDetail billDetail2 = new BillDetail();
        billDetail2.setBill(pending);
        billDetailRepository.save(billDetail2);

        BillDetail billDetail3 = new BillDetail();
        billDetail3.setBill(pay);
        billDetailRepository.save(billDetail3);

        // Act
        MedicalAct updated = medicalActRepository.save(oldMedicalAct.updatePrice(100.0));
        List<BillDetail> billDetailsUpdated = billDetailService.updateBillDetail(oldMedicalAct, updated);

        // Assert
        assertEquals(1, billDetailsUpdated.size());
        assertEquals(withMedicalAct1.getId(), billDetailsUpdated.get(0).getId());
        assertEquals(updated, billDetailsUpdated.get(0).getMedicalAct());
        Mockito.verify(billDetailRepository, times(1)).findAllByBill_StatusAndMedicalAct(Bill.Status.PENDING, oldMedicalAct);
    }
}
