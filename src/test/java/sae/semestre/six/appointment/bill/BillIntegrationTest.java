/*
 * BillIntegrationTest.java                                  20 mai 2025
 * IUT de Rodez, pas de droit d'auteur
 */

package sae.semestre.six.appointment.bill;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.TransactionSystemException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BillIntegrationTest {

    @Autowired
    private BillRepository billRepository;


    @Test
    void testBillIsUnmodifiable() {
        // Given a bill saved in DB
        Bill bill = billRepository.save(new Bill());

        // When we modify the bill and we try to save it
        bill.setBillNumber("BILL123");
        Runnable saveBill = () -> billRepository.save(bill);

        // Then an exception is thrown

        // And it contains a BillModifiedException as root cause
        Throwable rootCause = assertThrows(TransactionSystemException.class, saveBill::run);

        while (rootCause.getCause() != null && !(rootCause instanceof BillModifiedException)) {
            rootCause = rootCause.getCause();
        }

        assertInstanceOf(BillModifiedException.class, rootCause);
        assertEquals("Bill has been modified, your are not allowed to do that", rootCause.getMessage());
    }

    @Test
    void testBill() {
        // Given a bill saved in DB
        Bill bill = billRepository.save(new Bill());

        // When we try to save it
        Runnable saveBill = () -> billRepository.save(bill);

        // Then nothing is throwned
        assertDoesNotThrow(saveBill::run);
    }
}
