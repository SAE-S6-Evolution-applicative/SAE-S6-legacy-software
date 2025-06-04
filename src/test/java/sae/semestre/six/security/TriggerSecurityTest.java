package sae.semestre.six.security;

import jakarta.persistence.EntityManager;
import org.hibernate.exception.GenericJDBCException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
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
public class TriggerSecurityTest {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private BillDetailRepository billDetailRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void testPreventBillDeletion() {
        // Given
        Bill bill = billRepository.save(new Bill());

        // When & Then
        Throwable throwable = assertThrows(GenericJDBCException.class, () -> {
            entityManager.createQuery("delete from Bill b where b.id = " + bill.getId()).executeUpdate();
        });

        while (throwable.getCause() != null && !(throwable instanceof SQLException)) {
            throwable = throwable.getCause();
        }

        assertInstanceOf(SQLException.class, throwable);
        assertEquals("A bill cannot be deleted", throwable.getMessage());
    }

    @Test
    public void testPreventBillTotalAmountUpdate() {
        // Given
        Bill bill = new Bill();
        bill.setTotalAmount(10.0);
        bill = billRepository.save(bill);
        final Long billId = bill.getId();

        // When & Then
        Throwable throwable = assertThrows(GenericJDBCException.class, () -> {
            entityManager.createQuery("update Bill b set b.totalAmount = 1000 where b.id = " + billId).executeUpdate();
        });

        while (throwable.getCause() != null && !(throwable instanceof SQLException)) {
            throwable = throwable.getCause();
        }

        assertInstanceOf(SQLException.class, throwable);
        assertEquals("total_amount cannot be updated", throwable.getMessage());
    }

    @Test
    public void testPreventBillDetailDeletion() {
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
        Throwable throwable = assertThrows(GenericJDBCException.class, () -> {
            entityManager.createQuery("delete from BillDetail b where b.id = " + billDetailId).executeUpdate();
        });

        while (throwable.getCause() != null && !(throwable instanceof SQLException)) {
            throwable = throwable.getCause();
        }

        assertInstanceOf(SQLException.class, throwable);
        assertEquals("A bill detail cannot be deleted", throwable.getMessage());
    }

    @Test
    public void testPreventBillDetailUpdate() {
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
        Throwable throwable = assertThrows(GenericJDBCException.class, () -> {
            entityManager.createQuery("update BillDetail bd set bd.bill.id = 1000 where bd.id = " + billDetailId).executeUpdate();
        });
        while (throwable.getCause() != null && !(throwable instanceof SQLException)) {
            throwable = throwable.getCause();
        }
        assertInstanceOf(SQLException.class, throwable);
        assertEquals("bill_id cannot be updated", throwable.getMessage());

        // And When & Then
        throwable = assertThrows(GenericJDBCException.class, () -> {
            entityManager.createQuery("update BillDetail bd set bd.quantity = 1000 where bd.id = " + billDetailId).executeUpdate();
        });
        while (throwable.getCause() != null && !(throwable instanceof SQLException)) {
            throwable = throwable.getCause();
        }
        assertInstanceOf(SQLException.class, throwable);
        assertEquals("quantity cannot be updated", throwable.getMessage());

        // And When & Then
        throwable = assertThrows(GenericJDBCException.class, () -> {
            entityManager.createQuery("update BillDetail bd set bd.priceMedicalAct = 1000 where bd.id = " + billDetailId).executeUpdate();
        });
        while (throwable.getCause() != null && !(throwable instanceof SQLException)) {
            throwable = throwable.getCause();
        }
        assertInstanceOf(SQLException.class, throwable);
        assertEquals("price_medical_act cannot be updated", throwable.getMessage());

        // And When & Then
        throwable = assertThrows(GenericJDBCException.class, () -> {
            entityManager.createQuery("update BillDetail bd set bd.lineTotal = 1000 where bd.id = " + billDetailId).executeUpdate();
        });
        while (throwable.getCause() != null && !(throwable instanceof SQLException)) {
            throwable = throwable.getCause();
        }
        assertInstanceOf(SQLException.class, throwable);
        assertEquals("line_total cannot be updated", throwable.getMessage());
    }
} 