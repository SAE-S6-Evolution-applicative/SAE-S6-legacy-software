/*
 * BillIntegrationTest.java                                  20 mai 2025
 * IUT de Rodez, pas de droit d'auteur
 */

package sae.semestre.six.appointment.bill;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.TransactionSystemException;
import sae.semestre.six.appointment.medicalact.MedicalAct;
import sae.semestre.six.appointment.medicalact.MedicalActRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BillIntegrationTest {

    @Autowired
    private BillRepository billRepository;
    @Autowired
    private MedicalActRepository medicalActRepository;
    @Autowired
    private EntityManager entityManager;


    @Test
    void testBillIsUnmodifiable_ButTheTotalAmountHaveBeenUpdated() {
        // Given a bill saved in DB
        Bill bill = billRepository.save(new Bill());

        // When we modify the bill and we try to save it
        bill.setTotalAmount(1234567.0);
        Runnable saveBill = () -> {
            billRepository.save(bill);
        };

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
    void testBillIsUnmodifiable_ButTheBillDetailsHaveBeenUpdated() {
        // Given a bill saved in DB
        Bill bill = billRepository.save(new Bill());

        // When we modify the bill and we try to save it
        bill.addBillDetail(new BillDetail(new MedicalAct("Medical Act", 100.0), 1));
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

    @Test
    void testBillIsNotDeletable() {
        // Given a bill saved in DB
        Bill bill = billRepository.save(new Bill());

        // When we try to delete it
        Runnable deleteBill = () -> billRepository.delete(bill);

        // Then an exception is thrown
        assertThrows(BillCannotBeDeletedException.class, deleteBill::run);
    }
}
