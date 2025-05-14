package sae.semestre.six.appointment.bill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import sae.semestre.six.appointment.medicalact.MedicalAct;
import sae.semestre.six.appointment.medicalact.MedicalActRepository;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class BillDetailRepositoryTest {
    private BillRepository billRepository;
    private BillDetailRepository billDetailRepository;
    private MedicalActRepository medicalActRepository;

    @Autowired
    public BillDetailRepositoryTest(
            BillRepository billRepository,
            BillDetailRepository billDetailRepository,
            MedicalActRepository medicalActRepository
    ) {
        this.billRepository = billRepository;
        this.billDetailRepository = billDetailRepository;
        this.medicalActRepository = medicalActRepository;
    }

    @Test
    void testFindAllByPendingBillAndMedicalAct() {
        // Arrange
        MedicalAct medicalAct = medicalActRepository.save(new MedicalAct("Consultation", 50.0));

        Bill pendingWithMedical = new Bill();
        pendingWithMedical.setStatus(Bill.Status.PENDING);
        billRepository.save(pendingWithMedical);

        BillDetail withMedicalAct1 = new BillDetail();
        withMedicalAct1.setMedicalAct(medicalAct);
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
        List<BillDetail> billDetails = billDetailRepository.findAllByPendingBillAndMedicalAct(medicalAct);

        // Assert
        assertEquals(1, billDetails.size());
        assertEquals(withMedicalAct1.getId(), billDetails.get(0).getId());
    }
}