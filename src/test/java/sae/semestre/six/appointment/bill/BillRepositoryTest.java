package sae.semestre.six.appointment.bill;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import sae.semestre.six.appointment.medicalact.MedicalAct;
import sae.semestre.six.appointment.medicalact.MedicalActRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class BillRepositoryTest {

    private BillRepository billRepository;
    private BillDetailRepository billDetailRepository;
    private MedicalActRepository medicalActRepository;

    @Autowired
    public BillRepositoryTest(
            BillRepository billRepository,
            BillDetailRepository billDetailRepository,
            MedicalActRepository medicalActRepository
    ) {
        this.billRepository = billRepository;
        this.billDetailRepository = billDetailRepository;
        this.medicalActRepository = medicalActRepository;
    }

    @Test
    void test() {
        // Given a bill with a medical act in their details
        MedicalAct medicalAct = medicalActRepository.save(new MedicalAct("ACT1", 10.0));
        Bill bill30 = billRepository.save(new Bill());
        BillDetail billDetail30 = billDetailRepository.save(new BillDetail(bill30, medicalAct, 3));

        // When the bill is saved
        bill30 = billRepository.save(bill30.addBillDetail(billDetail30));

        // Then the bill should be saved with the correct details
        assertEquals(1, bill30.getBillDetails().size());
        assertEquals(30, bill30.getTotalAmount());
        assertEquals(30, billRepository.findTotalRevenue());

        // When a new medical act is added to the bill
        medicalAct = medicalActRepository.save(new MedicalAct("ACT2", 100.0));
        Bill bill130 = billRepository.save(new Bill());
        BillDetail billDetail100 = billDetailRepository.save(new BillDetail(bill130, medicalAct, 1));

        bill130 = billRepository.save(
                bill130.addBillDetail(billDetail30)
                        .addBillDetail(billDetail100)
        );

        // Then...
        assertEquals(2, billDetailRepository.count());
        assertNotEquals(bill30.getId(), bill130.getId());

        assertEquals(2, bill130.getBillDetails().size());
        assertEquals(130, bill130.getTotalAmount());
        assertEquals(160, billRepository.findTotalRevenue());
    }
}
