package sae.semestre.six.appointment.patient.history;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sae.semestre.six.appointment.Appointment;
import sae.semestre.six.appointment.bill.Bill;
import sae.semestre.six.appointment.patient.Patient;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class PatientHistoryTest {

    private PatientHistory patientHistory;

    @Mock
    private Bill bill1;

    @Mock
    private Bill bill2;

    @Mock
    private Appointment appointment1;

    @Mock
    private Appointment appointment2;

    @Mock
    private Patient patient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        patientHistory = new PatientHistory();
    }

    @Test
    void testGetBillsSortedShouldReturnBillsInDescendingOrderOfDate() {
        // Arrange
        LocalDateTime date1 = LocalDateTime.of(2023, 1, 1, 1, 1);
        LocalDateTime date2 = LocalDateTime.of(2023, 2, 1, 1, 1);
        when(bill1.getBillDate()).thenReturn(date1);
        when(bill2.getBillDate()).thenReturn(date2);

        patientHistory.getBills().add(bill1);
        patientHistory.getBills().add(bill2);

        // Act
        List<Bill> sortedBills = patientHistory.getBillsSorted();

        // Assert
        assertEquals(2, sortedBills.size());
        assertEquals(bill2, sortedBills.get(0)); // Newer bill comes first
        assertEquals(bill1, sortedBills.get(1)); // Older bill comes second
    }

    @Test
    void testGetTotalBilledAmountShouldReturnCorrectSum() {
        // Arrange
        when(bill1.getTotalAmount()).thenReturn(100.0);
        when(bill2.getTotalAmount()).thenReturn(200.0);

        patientHistory.getBills().add(bill1);
        patientHistory.getBills().add(bill2);

        // Act
        Double totalBilledAmount = patientHistory.getTotalBilledAmount();

        // Assert
        assertEquals(300.0, totalBilledAmount);
    }

    @Test
    void testSetAndGetPatient() {
        // Set Patient
        patientHistory.setPatient(patient);

        // Get Patient
        assertEquals(patient, patientHistory.getPatient());
    }

    @Test
    void testSetAndGetVisitDate() {
        // Arrange
        LocalDateTime visitDate = LocalDateTime.now();

        // Act
        patientHistory.setVisitDate(visitDate);

        // Assert
        assertEquals(visitDate, patientHistory.getVisitDate());
    }

    @Test
    void testSetAndGetDiagnosis() {
        // Arrange
        String diagnosis = "Test Diagnosis";

        // Act
        patientHistory.setDiagnosis(diagnosis);

        // Assert
        assertEquals(diagnosis, patientHistory.getDiagnosis());
    }

    @Test
    void testSetAndGetSymptoms() {
        // Arrange
        String symptoms = "Test Symptoms";

        // Act
        patientHistory.setSymptoms(symptoms);

        // Assert
        assertEquals(symptoms, patientHistory.getSymptoms());
    }

    @Test
    void testSetAndGetNotes() {
        // Arrange
        String notes = "Test Notes";

        // Act
        patientHistory.setNotes(notes);

        // Assert
        assertEquals(notes, patientHistory.getNotes());
    }
}