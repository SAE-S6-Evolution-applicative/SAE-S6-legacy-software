/*
 * BillDetailIntegrationTest.java                                  21 mai 2025
 * IUT de Rodez, pas de droit d'auteur
 */

package sae.semestre.six.appointment.bill;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Transactional;
import sae.semestre.six.appointment.medicalact.MedicalAct;
import sae.semestre.six.appointment.medicalact.MedicalActRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BillDetailIntegrationTest {

    @Autowired
    private BillDetailRepository billDetailRepository;
    @Autowired
    private BillRepository billRepository;
    @Autowired
    private MedicalActRepository medicalActRepository;

    @Test
    void testBillDetailIsUnmodifiable_ButTheQuantityHaveBeenUpdated() {
        // Given a bill detail saved in db
        BillDetail billDetail = new BillDetail(new MedicalAct("Medical Act", 100.0), 2);
        Bill bill = new Bill();
        bill.addBillDetail(billDetail);
        billRepository.save(bill);


        // When the quantity is modified
        billDetail.setQuantity(3);
        Runnable saveBillDetail = () -> billDetailRepository.save(billDetail);

        // Then an exception is thrown
        // And it contains a BillModifiedException as the root cause
        Throwable rootCause = assertThrows(TransactionSystemException.class, saveBillDetail::run);

        while (rootCause.getCause() != null && !(rootCause instanceof BillModifiedException)) {
            rootCause = rootCause.getCause();
        }

        assertInstanceOf(BillModifiedException.class, rootCause);
        assertEquals("Bill detail has been modified, your are not allowed to do that", rootCause.getMessage());
    }

    @Test
    void testBillDetailIsUnmodifiable_ButTheLineTotalHaveBeenUpdated() {
        // Given a bill detail saved in db
        BillDetail billDetail = new BillDetail(new MedicalAct("Medical Act", 100.0), 2);
        Bill bill = new Bill();
        bill.addBillDetail(billDetail);
        billRepository.save(bill);


        // When the quantity is modified
        billDetail.setLineTotal(1000.0);
        Runnable saveBillDetail = () -> {
            billDetailRepository.save(billDetail);
        };

        // Then an exception is thrown
        // And it contains a BillModifiedException as the root cause
        Throwable rootCause = assertThrows(TransactionSystemException.class, saveBillDetail::run);

        while (rootCause.getCause() != null && !(rootCause instanceof BillModifiedException)) {
            rootCause = rootCause.getCause();
        }

        assertInstanceOf(BillModifiedException.class, rootCause);
        assertEquals("Bill detail has been modified, your are not allowed to do that", rootCause.getMessage());
    }

    @Test
    void testBillDetailIsNotDeletable() {
        // Given a bill saved in DB
        Bill bill = new Bill();
        BillDetail billDetail = new BillDetail();
        bill.addBillDetail(billDetail);
        billRepository.save(bill);

        // When we try to delete it
        Runnable deleteBillDetail = () -> billDetailRepository.delete(billDetail);

        // Then an exception is thrown
        assertThrows(BillCannotBeDeletedException.class, deleteBillDetail::run);
    }
}
