package sae.semestre.six.appointment.prescription;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
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
        LocalDateTime lastModifiedBefore = prescription.getLastModified();

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
        LocalDateTime initialLastModified = prescription.getLastModified();

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

    @Test
    void testUpdatesLastModifiedOnSetBilled() {
        // Given
        Prescription prescription = new Prescription();
        LocalDateTime lastModifiedBefore = prescription.getLastModified();
        
        // Pause to ensure timestamps are different
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // When
        prescription.setBilled(true);
        
        // Then
        assertNotEquals(lastModifiedBefore, prescription.getLastModified());
    }

    @Test
    void testPreUpdateMethodUpdatesLastModified() {
        // Given
        Prescription prescription = new Prescription();
        LocalDateTime initialLastModified = prescription.getLastModified();

        // Pause to ensure timestamps are different
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // When
        prescription.preUpdate();
        
        // Then
        assertNotEquals(initialLastModified, prescription.getLastModified());
    }
}