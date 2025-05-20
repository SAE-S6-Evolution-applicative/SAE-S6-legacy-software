package sae.semestre.six.appointment.medicalact;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MedicalActTest {

    @Test
    void updatePrice() {
        // Given a medical act
        MedicalAct medicalAct = new MedicalAct("Test Act", 100.0);

        // When we try to update the medical act price
        double updatePrice = 150.0;
        MedicalAct updated = medicalAct.updatePrice(updatePrice);

        // Then...
        assertNotNull(updated);
        assertEquals(updatePrice, updated.getPrice());
        assertEquals(medicalAct.getName(), updated.getName());
        assertNotSame(medicalAct, updated);
        assertNotEquals(medicalAct.getPrice(), updated.getPrice());
        assertFalse(medicalAct.isActive());
        assertTrue(updated.isActive());
    }
}