/*
 * InsuranceTest.java                                 12 mai 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.patient;

import org.junit.jupiter.api.Test;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class InsuranceTest {

    @Test
    void testCalculateCoverageWithinMaximum() {
        // Given
        Insurance insurance = new Insurance();
        insurance.setCoveragePercentage(80.0);
        insurance.setMaxCoverage(1000.0);

        // When
        Double coverage = insurance.calculateCoverage(1000.0);

        // Then
        assertEquals(800.0, coverage);
    }

    @Test
    void testCalculateCoverageExceedsMaximum() {
        // Given
        Insurance insurance = new Insurance();
        insurance.setCoveragePercentage(80.0);
        insurance.setMaxCoverage(500.0);

        // When
        Double coverage = insurance.calculateCoverage(1000.0);

        // Then
        assertEquals(500.0, coverage);
    }

    @Test
    void testIsValidWithFutureExpiryDate() {
        // Given
        Insurance insurance = new Insurance();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 3); // Future date
        Date futureDate = calendar.getTime();
        insurance.setExpiryDate(futureDate);

        // When
        boolean valid = insurance.isValid();

        // Then
        assertTrue(valid);
    }

    @Test
    void testIsValidWithPastExpiryDate() {
        // Given
        Insurance insurance = new Insurance();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -3); // Past date
        Date pastDate = calendar.getTime();
        insurance.setExpiryDate(pastDate);

        // When
        boolean valid = insurance.isValid();

        // Then
        assertFalse(valid);
    }

    @Test
    void testGettersAndSetters() {
        // Given
        Insurance insurance = new Insurance();
        Patient patient = new Patient();
        Date expiryDate = new Date();

        // When
        insurance.setId(1L);
        insurance.setPolicyNumber("POL123456");
        insurance.setPatient(patient);
        insurance.setProvider("Health Insurance Inc.");
        insurance.setCoveragePercentage(75.0);
        insurance.setMaxCoverage(2000.0);
        insurance.setExpiryDate(expiryDate);

        // Then
        assertEquals(1L, insurance.getId());
        assertEquals("POL123456", insurance.getPolicyNumber());
        assertEquals(patient, insurance.getPatient());
        assertEquals("Health Insurance Inc.", insurance.getProvider());
        assertEquals(75.0, insurance.getCoveragePercentage());
        assertEquals(2000.0, insurance.getMaxCoverage());
        assertEquals(expiryDate, insurance.getExpiryDate());
    }
}
