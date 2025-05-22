package sae.semestre.six.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import sae.semestre.six.appointment.bill.Bill;
import sae.semestre.six.appointment.bill.BillDetail;
import sae.semestre.six.appointment.bill.BillRepository;
import sae.semestre.six.appointment.bill.BillDetailRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class TriggerSecurityTest {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private BillDetailRepository billDetailRepository;

    @Test
    public void testPreventBillDeletion() {
        // Given
        Bill bill = billRepository.findById(1L).orElseThrow();

        // When & Then
        assertThrows(Exception.class, () -> {
            billRepository.delete(bill);
        }, "Should not be able to delete a bill");
    }

    @Test
    public void testPreventBillTotalAmountUpdate() {
        // Given
        Bill bill = billRepository.findById(1L).orElseThrow();
        double originalAmount = bill.getTotalAmount();
        ReflectionTestUtils.setField(bill, "totalAmount", originalAmount + 100.0);

        // When & Then
        assertThrows(Exception.class, () -> {
            billRepository.save(bill);
        }, "Should not be able to update bill total amount");
    }

    @Test
    public void testPreventBillDetailDeletion() {
        // Given
        BillDetail billDetail = billDetailRepository.findById(1L).orElseThrow();

        // When & Then
        assertThrows(Exception.class, () -> {
            billDetailRepository.delete(billDetail);
        }, "Should not be able to delete a bill detail");
    }

    @Test
    public void testPreventBillDetailUpdate() {
        // Given
        BillDetail billDetail = billDetailRepository.findById(1L).orElseThrow();
        
        // Test quantity update
        ReflectionTestUtils.setField(billDetail, "quantity", billDetail.getQuantity() + 1);
        assertThrows(Exception.class, () -> {
            billDetailRepository.save(billDetail);
        }, "Should not be able to update quantity");

        // Test line total update
        ReflectionTestUtils.setField(billDetail, "lineTotal", billDetail.getLineTotal() + 10.0);
        assertThrows(Exception.class, () -> {
            billDetailRepository.save(billDetail);
        }, "Should not be able to update line total");
    }
} 