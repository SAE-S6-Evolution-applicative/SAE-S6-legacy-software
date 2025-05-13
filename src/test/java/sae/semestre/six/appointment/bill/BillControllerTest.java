package sae.semestre.six.appointment.bill;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BillControllerTest {
    
    private BillController billController = BillController.getInstance();
    
    @Test
    public void testProcessBill() {
        
        File billingFile = new File("C:\\hospital\\billing.txt");
        long initialFileSize = billingFile.length();
        
        String result = billController.processBill(
            "TEST001",
            "DOC001",
            new String[]{"CONSULTATION"}
        );
        
        assertTrue(result.contains("successfully"));
        assertTrue(billingFile.length() > initialFileSize);
    }
    
    @Test
    public void testCalculateInsurance() {
        
        double result = Double.parseDouble(
            billController.calculateInsurance(1000.0)
                .replace("Insurance coverage: $", "")
        );
        
        assertEquals(700.0, result, 0.01);
    }
    
    @Test
    public void testUpdatePrice() {
        billController.updatePrice("CONSULTATION", 75.0);
        assertEquals(75.0, billController.getPrices().get("CONSULTATION"), 0.01);
    }
}
