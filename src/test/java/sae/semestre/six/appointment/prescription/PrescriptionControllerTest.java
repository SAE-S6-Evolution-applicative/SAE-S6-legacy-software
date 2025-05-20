package sae.semestre.six.appointment.prescription;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

public class PrescriptionControllerTest {

    @Autowired
    private PrescriptionController prescriptionController;


    @Test
    public void testAddAndRetrievePrescription() {
        String result = prescriptionController.addPrescription(
                "PAT001",
                new String[]{"PARACETAMOL"},
                "Test notes"
        );

        assertTrue(result.contains("created"));

        List<String> prescriptions = prescriptionController.getPatientPrescriptions("PAT001");
        assertFalse(prescriptions.isEmpty());


        assertTrue(prescriptions.get(0).startsWith("RX"));
    }


    @Test
    public void testInventory() {
        prescriptionController.refillMedicine("PARACETAMOL", 10);
        assertEquals(10, (int) prescriptionController.getInventory().get("PARACETAMOL"));
    }


    @Test
    public void testClearData() {
        prescriptionController.clearAllData();
        assertTrue(prescriptionController.getInventory().isEmpty());
    }
} 