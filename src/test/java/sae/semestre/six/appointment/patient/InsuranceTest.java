/*
 * InsuranceTest.java                                 12 mai 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.patient;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

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
    public void testIsValid_WithFutureDate() {
        // Given
        Insurance insurance = new Insurance();
        LocalDate futureDate = LocalDate.now().plusDays(30);
        insurance.setExpiryDate(futureDate);
        
        // When
        boolean isValid = insurance.isValid();
        
        // Then
        assertTrue(isValid);
    }
    
    @Test
    public void testIsValid_WithPastDate() {
        // Given
        Insurance insurance = new Insurance();
        LocalDate pastDate = LocalDate.now().minusDays(30);
        insurance.setExpiryDate(pastDate);
        
        // When
        boolean isValid = insurance.isValid();
        
        // Then
        assertFalse(isValid);
    }

    @Test
    void testSetAndGetExpiryDate() {
        LocalDate expiryDate = LocalDate.now();
        Insurance insurance = new Insurance();
        insurance.setExpiryDate(expiryDate);
        assertEquals(expiryDate, insurance.getExpiryDate());
    }

    @Test
    void testGettersAndSetters() {
        // Given
        Insurance insurance = new Insurance();
        Patient patient = new Patient();
        LocalDate expiryDate = LocalDate.now();

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
