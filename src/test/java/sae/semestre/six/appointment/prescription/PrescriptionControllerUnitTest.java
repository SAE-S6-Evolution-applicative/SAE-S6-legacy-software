package sae.semestre.six.appointment.prescription;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sae.semestre.six.appointment.prescription.PrescriptionController.PrescriptionRequest;

import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
public class PrescriptionControllerUnitTest {

    @Autowired
    private PrescriptionController prescriptionController;

    @Test
    public void testAddAndRetrievePrescription() {
        PrescriptionRequest prescriptionRequest = new PrescriptionRequest(1L, List.of(1L,2L,3L),"Notes prescription");
        prescriptionController.addPrescription(prescriptionRequest);

        // Supposons que getPatientPrescriptions attend un Long, pas un String
        List<PrescriptionResponse> prescriptions = prescriptionController.getPatientPrescriptions(1L);
        assertNotNull(prescriptions);
        assertFalse(prescriptions.isEmpty());
        assertTrue(prescriptions.getFirst().getPrescriptionNumber().startsWith("RX"));
    }

    @Test
    public void testGetPatientPrescriptionsReturnsEmptyList() {
        List<PrescriptionResponse> prescriptions = prescriptionController.getPatientPrescriptions(999L);
        assertNotNull(prescriptions);
        assertTrue(prescriptions.isEmpty());
    }

    @Test
    public void testCalculateCost() {
        PrescriptionRequest prescriptionRequest = new PrescriptionRequest(1L, List.of(1L,2L,3L),"Notes prescription");
        prescriptionController.addPrescription(prescriptionRequest);

        List<PrescriptionResponse> prescriptions = prescriptionController.getPatientPrescriptions(2L);
        assertFalse(prescriptions.isEmpty());
        PrescriptionResponse prescription = prescriptions.getFirst();
        double cost = prescriptionController.calculateCost(prescription.getId());
        assertTrue(cost >= 0);
    }
} 