package sae.semestre.six.security;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.hibernate.exception.GenericJDBCException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import sae.semestre.six.appointment.bill.Bill;
import sae.semestre.six.appointment.bill.BillDetail;
import sae.semestre.six.appointment.bill.BillDetailRepository;
import sae.semestre.six.appointment.bill.BillRepository;
import sae.semestre.six.appointment.medicalact.MedicalAct;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class TriggerSecurityTest {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private BillDetailRepository billDetailRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void testPreventBillDeletion() {
        // Given
        Bill bill = billRepository.save(new Bill());

        // When & Then
        Query deleteQuery = entityManager.createQuery("delete from Bill b where b.id = " + bill.getId());
        Throwable throwable = assertThrows(GenericJDBCException.class, deleteQuery::executeUpdate);

        while (throwable.getCause() != null && !(throwable instanceof SQLException)) {
            throwable = throwable.getCause();
        }

        assertInstanceOf(SQLException.class, throwable);
        assertEquals("A bill cannot be deleted", throwable.getMessage());
    }

    @Test
    void testPreventBillTotalAmountUpdate() {
        // Given
        Bill bill = new Bill();
        bill.setTotalAmount(10.0);
        bill = billRepository.save(bill);
        final Long billId = bill.getId();

        // When & Then
        Query updateTotalAmount = entityManager.createQuery("update Bill b set b.totalAmount = 1000 where b.id = " + billId);
        Throwable throwable = assertThrows(GenericJDBCException.class, updateTotalAmount::executeUpdate);

        while (throwable.getCause() != null && !(throwable instanceof SQLException)) {
            throwable = throwable.getCause();
        }

        assertInstanceOf(SQLException.class, throwable);
        assertEquals("total_amount cannot be updated", throwable.getMessage());
    }

    @Test
    void testPreventBillDetailDeletion() {
        // Given
        MedicalAct medicalAct = new MedicalAct("Medical Act", 100.0);
        Bill bill = new Bill();
        BillDetail billDetail = new BillDetail();
        billDetail.setMedicalAct(medicalAct);
        bill.addBillDetail(billDetail);
        bill = billRepository.save(bill);
        billDetail = bill.getBillDetails().stream().findAny().get();
        Long billDetailId = billDetail.getId();

        // When & Then
        Query deleteBillDetail = entityManager.createQuery("delete from BillDetail b where b.id = " + billDetailId);
        Throwable throwable = assertThrows(GenericJDBCException.class, deleteBillDetail::executeUpdate);

        while (throwable.getCause() != null && !(throwable instanceof SQLException)) {
            throwable = throwable.getCause();
        }

        assertInstanceOf(SQLException.class, throwable);
        assertEquals("A bill detail cannot be deleted", throwable.getMessage());
    }

    @Test
    void testPreventBillDetailUpdate() {
        // Given
        MedicalAct medicalAct = new MedicalAct("Medical Act", 100.0);
        Bill bill = new Bill();
        BillDetail billDetail = new BillDetail();
        billDetail.setMedicalAct(medicalAct);
        billDetail.setLineTotal(0.0);
        bill.addBillDetail(billDetail);
        billRepository.save(bill);
        billDetail = billDetailRepository.findAll().get(0);
        Long billDetailId = billDetail.getId();

        // When & Then
        Query updateBillId = entityManager.createQuery("update BillDetail bd set bd.bill.id = 1000 where bd.id = " + billDetailId);
        Throwable throwable = assertThrows(GenericJDBCException.class, updateBillId::executeUpdate);
        while (throwable.getCause() != null && !(throwable instanceof SQLException)) {
            throwable = throwable.getCause();
        }
        assertInstanceOf(SQLException.class, throwable);
        assertEquals("bill_id cannot be updated", throwable.getMessage());

        // And When & Then
        Query updateQuantity = entityManager.createQuery("update BillDetail bd set bd.quantity = 1000 where bd.id = " + billDetailId);
        throwable = assertThrows(GenericJDBCException.class, updateQuantity::executeUpdate);
        while (throwable.getCause() != null && !(throwable instanceof SQLException)) {
            throwable = throwable.getCause();
        }
        assertInstanceOf(SQLException.class, throwable);
        assertEquals("quantity cannot be updated", throwable.getMessage());

        // And When & Then
        Query updateMedicalActPrice = entityManager.createQuery("update BillDetail bd set bd.priceMedicalAct = 1000 where bd.id = " + billDetailId);
        throwable = assertThrows(GenericJDBCException.class, updateMedicalActPrice::executeUpdate);
        while (throwable.getCause() != null && !(throwable instanceof SQLException)) {
            throwable = throwable.getCause();
        }
        assertInstanceOf(SQLException.class, throwable);
        assertEquals("price_medical_act cannot be updated", throwable.getMessage());

        // And When & Then
        Query updateLineTotal = entityManager.createQuery("update BillDetail bd set bd.lineTotal = 1000 where bd.id = " + billDetailId);
        throwable = assertThrows(GenericJDBCException.class, updateLineTotal::executeUpdate);
        while (throwable.getCause() != null && !(throwable instanceof SQLException)) {
            throwable = throwable.getCause();
        }
        assertInstanceOf(SQLException.class, throwable);
        assertEquals("line_total cannot be updated", throwable.getMessage());
    }
} 