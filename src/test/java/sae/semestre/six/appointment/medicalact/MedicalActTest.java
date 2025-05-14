package sae.semestre.six.appointment.medicalact;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MedicalActTest {

    @Test
    void updatePrice() {
        MedicalAct medicalAct = new MedicalAct("Test Act", 100.0);

        double updatePrice = 150.0;
        MedicalAct updated = medicalAct.updatePrice(updatePrice);

        assertNotNull(updated);
        assertEquals(updatePrice, updated.getPrice());
        assertEquals(medicalAct.getName(), updated.getName());
        assertNotSame(medicalAct, updated);
        assertNotEquals(medicalAct.getPrice(), updated.getPrice());
        assertFalse(medicalAct.isActive());
        assertTrue(updated.isActive());
    }
}