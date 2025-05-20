package sae.semestre.six.appointment.bill;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import sae.semestre.six.appointment.patient.Patient;
import sae.semestre.six.email.EmailService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class BillDaoImplIntegrationTest {

    @Autowired
    private BillRepository billRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private EmailService emailService;

    @Test
    void testFindByBillNumber() {
        // Given
        Bill bill = new Bill();
        bill.setBillNumber("BILL001");
        bill.setTotalAmount(100.0);
        bill.setStatus(Bill.Status.PENDING);
        entityManager.persist(bill);
        entityManager.flush();

        // When
        Bill foundBill = billRepository.findBillByBillNumber("BILL001");

        // Then
        assertNotNull(foundBill);
        assertEquals(bill.getBillNumber(), foundBill.getBillNumber());
    }

    @Test
    void testFindByPatientId() {
        // Given
        Patient patient = new Patient();
        patient.setPatientNumber("PAT001");
        patient.setFirstName("John");
        patient.setLastName("Doe");
        entityManager.persist(patient);
        entityManager.flush();

        Bill bill = new Bill();
        bill.setPatient(patient);
        entityManager.persist(bill);
        entityManager.flush();

        // When
        List<Bill> bills = billRepository.findBillsByPatient_Id(patient.getId());

        // Then
        assertEquals(1, bills.size());
        assertEquals(bill.getId(), bills.get(0).getId());
    }

}
