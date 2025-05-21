package sae.semestre.six.appointment.bill;

import org.junit.jupiter.api.Test;
import sae.semestre.six.appointment.medicalact.MedicalAct;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BillDetailTest {

    @Test
    void testSetAndGetTreatmentName() {
        // Given
        BillDetail detail = new BillDetail();
        MedicalAct consultation = new MedicalAct("Consultation", 50.0);
        // When
        detail.setMedicalAct(consultation);
        // Then
        assertEquals("Consultation", detail.getNameMedicalAct());
    }

    @Test
    void testCalculateLineTotal() {
        // Given
        MedicalAct consultation = new MedicalAct("Consultation", 50.0);
        BillDetail detail = new BillDetail();
        detail.setMedicalAct(consultation);
        detail.setQuantity(2);
        // When
        detail.calculateLineTotal();
        // Then
        assertEquals(100.0, detail.getLineTotal());
    }
}
