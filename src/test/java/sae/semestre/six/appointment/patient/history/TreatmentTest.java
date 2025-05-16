/*
 * TreatmentTest.java                                 13 mai 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.patient.history;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TreatmentTest {

    private Treatment treatment;

    @BeforeEach
    void setUp() {
        treatment = new Treatment();
    }

    @Test
    void testSetAndGetId() {
        // Given
        Long id = 1L;

        // When
        treatment.setId(id);

        // Then
        assertEquals(id, treatment.getId());
    }

    @Test
    void testSetAndGetName() {
        // Given
        String name = "Physiotherapy";

        // When
        treatment.setName(name);

        // Then
        assertEquals(name, treatment.getName());
    }

    @Test
    void testSetAndGetPatientHistory() {
        // Given
        PatientHistory patientHistory = new PatientHistory();

        // When
        treatment.setPatientHistory(patientHistory);

        // Then
        assertEquals(patientHistory, treatment.getPatientHistory());
    }

    @Test
    void testSetAndGetTreatmentDate() {
        // Given
        LocalDateTime treatmentDate = LocalDateTime.now();

        // When
        treatment.setTreatmentDate(treatmentDate);

        // Then
        assertEquals(treatmentDate, treatment.getTreatmentDate());
    }

    @Test
    void testSetAndGetNotes() {
        // Given
        String notes = "30 minutes session";

        // When
        treatment.setNotes(notes);

        // Then
        assertEquals(notes, treatment.getNotes());
    }
}