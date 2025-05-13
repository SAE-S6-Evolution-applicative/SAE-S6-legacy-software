package sae.semestre.six.appointment.prescription;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

class PrescriptionTest {

    @Test
    void testSetBilledSetsBilledStatusToTrue() {
        // Arrange
        Prescription prescription = new Prescription();
        Boolean initialBilledStatus = prescription.getBilled();

        // Act
        prescription.setBilled(true);

        // Assert
        assertFalse(initialBilledStatus, "Initial billed status should be false");
        assertTrue(prescription.getBilled(), "Billed status should be true after setting it to true");
        assertNotNull(prescription.getLastModified(), "LastModified date should not be null");
    }

    @Test
    void testSetBilledSetsBilledStatusToFalse() {
        // Arrange
        Prescription prescription = new Prescription();
        prescription.setBilled(true); // Set to true to verify toggle back to false
        Date lastModifiedBefore = prescription.getLastModified();

        // Simulate some time passing
        await().atMost(200, TimeUnit.MILLISECONDS).until(() -> {
            // Act
            prescription.setBilled(false);
            return true;
        });


        // Assert
        assertFalse(prescription.getBilled(), "Billed status should be false after setting it to false");
        assertNotEquals(lastModifiedBefore, prescription.getLastModified(), "LastModified date should be updated");
    }

    @Test
    void testSetBilledUpdatesLastModifiedDate() {
        // Arrange
        Prescription prescription = new Prescription();
        Date initialLastModified = prescription.getLastModified();

        // Simulate some time passing
        await().atMost(200, TimeUnit.MILLISECONDS).until(() -> {
            // Act
            prescription.setBilled(false);
            return true;
        });

        // Assert
        assertNotNull(prescription.getLastModified(), "LastModified date should not be null");
        assertNotEquals(initialLastModified, prescription.getLastModified(), "LastModified date should be updated");
    }
}