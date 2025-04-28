package sae.semestre.six.processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MedicalBillingProcessorTest {


    private MedicalBillingProcessor medicalBillingProcessor;

    @BeforeEach
    void setUp() {
        medicalBillingProcessor = MedicalBillingProcessor.getInstance();
    }

    @Test
    void testGetInstance() {
        assertNotNull(medicalBillingProcessor, "The instance should not be null");
        assertSame(medicalBillingProcessor, MedicalBillingProcessor.getInstance());
    }

    @Test
    void testProcessBilling() {
        // Given a patient with a specific ID
        String patientId = "1234";
        String doctorId = "5678";
        String[] treatment = {"CONSULTATION", "XRAY"};
        List<String> beforeBendingBill = getPendingBills();
        double beforeTotalRevenue = getTotalRevenue();

        // When we process the billing
        medicalBillingProcessor.processBilling(patientId, doctorId, treatment);

        // Then pendingBills and totalRevenue are updated
        List<String> afterBendingBill = getPendingBills();
        double afterTotalRevenue = getTotalRevenue();
        assertIterableEquals(beforeBendingBill, afterBendingBill, "Pending bills should be updated");
        assertNotEquals(beforeTotalRevenue, afterTotalRevenue, "Total revenue should be updated");

    }

    private double getTotalRevenue() {
        return (double) ReflectionTestUtils.getField(medicalBillingProcessor, "totalRevenue");
    }

    private List<String> getPendingBills() {
        return (List<String>) ReflectionTestUtils.getField(medicalBillingProcessor, "pendingBills");
    }

    @Test
    void testCalculateInsurance() {
        assertEquals(0, medicalBillingProcessor.calculateInsurance(-1), "Insurance should be 0");
        assertEquals(0, medicalBillingProcessor.calculateInsurance(0), "Insurance should be 0");
        assertEquals(0, medicalBillingProcessor.calculateInsurance(1), "Insurance should be 0");
    }





}