package sae.semestre.six.appointment.bill;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BillDetailTest {

    @Test
    void testSetAndGetTreatmentName() {
        // Given
        BillDetail detail = new BillDetail();
        // When
        detail.setTreatmentName("Consultation");
        // Then
        assertEquals("Consultation", detail.getTreatmentName());
    }

    @Test
    void testCalculateLineTotal() {
        // Given
        BillDetail detail = new BillDetail();
        detail.setQuantity(2);
        detail.setUnitPrice(50.0);
        // When
        detail.calculateLineTotal();
        // Then
        assertEquals(100.0, detail.getLineTotal());
    }
}
