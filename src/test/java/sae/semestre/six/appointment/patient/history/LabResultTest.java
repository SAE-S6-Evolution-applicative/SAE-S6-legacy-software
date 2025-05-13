/*
 * LabResultTest.java                                 13 mai 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.patient.history;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class LabResultTest {

    private LabResult labResult;

    @BeforeEach
    void setUp() {
        labResult = new LabResult();
    }

    @Test
    void testSetAndGetId() {
        // Given
        Long id = 1L;

        // When
        labResult.setId(id);

        // Then
        assertEquals(id, labResult.getId());
    }

    @Test
    void testSetAndGetPatientHistory() {
        // Given
        PatientHistory patientHistory = new PatientHistory();

        // When
        labResult.setPatientHistory(patientHistory);

        // Then
        assertEquals(patientHistory, labResult.getPatientHistory());
    }

    @Test
    void testSetAndGetTestName() {
        // Given
        String testName = "Blood Test";

        // When
        labResult.setTestName(testName);

        // Then
        assertEquals(testName, labResult.getTestName());
    }

    @Test
    void testSetAndGetResultValue() {
        // Given
        String resultValue = "120 mg/dL";

        // When
        labResult.setResultValue(resultValue);

        // Then
        assertEquals(resultValue, labResult.getResultValue());
    }

    @Test
    void testSetAndGetTestDate() {
        // Given
        Date testDate = new Date();

        // When
        labResult.setTestDate(testDate);

        // Then
        assertEquals(testDate, labResult.getTestDate());
    }

    @Test
    void testSetAndGetNotes() {
        // Given
        String notes = "Patient was fasting";

        // When
        labResult.setNotes(notes);

        // Then
        assertEquals(notes, labResult.getNotes());
    }
}