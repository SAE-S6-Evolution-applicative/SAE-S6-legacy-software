package sae.semestre.six.appointment.bill;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BillTest {

    @Test
    void testSetAndGetBillNumber() {
        // Given
        Bill bill = new Bill();
        // When
        bill.setBillNumber("BILL123");
        // Then
        assertEquals("BILL123", bill.getBillNumber());
    }

    @Test
    void testSetAndGetTotalAmount() {
        // Given
        Bill bill = new Bill();
        // When
        bill.setTotalAmount(150.0);
        // Then
        assertEquals(150.0, bill.getTotalAmount());
    }

    @Test
    void testSetAndGetStatus() {
        // Given
        Bill bill = new Bill();
        // When
        bill.setStatus("PAID");
        // Then
        assertEquals("PAID", bill.getStatus());
    }
}
