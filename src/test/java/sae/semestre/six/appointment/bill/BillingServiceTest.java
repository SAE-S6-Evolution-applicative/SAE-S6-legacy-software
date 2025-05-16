/*
 * BillingServiceTest.java                                 12 mai 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.bill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class BillingServiceTest {

    @InjectMocks
    private BillingService billingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessBill() {
        // Method is empty, we simply testinf that it does not generate any exception
        assertDoesNotThrow(() -> {
            billingService.processBill("1", "test", new String[]{"CONSULTATION"});
        });
    }

    @Test
    void testProcessBillWithNullValues() {
        // Verify that the method handle null values correctly
        assertDoesNotThrow(() -> {
            billingService.processBill(null, null, null);
        });
    }
}