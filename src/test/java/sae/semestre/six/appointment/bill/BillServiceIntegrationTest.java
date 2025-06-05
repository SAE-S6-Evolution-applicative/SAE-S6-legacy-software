/*
 * BillingServiceTest.java                                 12 mai 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.bill;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Transactional
class BillServiceIntegrationTest {

    private BillService billService;

    @Autowired
    public BillServiceIntegrationTest(
            BillService billService
    ) {
        this.billService = billService;
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
}