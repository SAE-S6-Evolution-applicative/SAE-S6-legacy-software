/*
 * BillHashTest.java                                  05 juin. 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.bill;

import org.junit.jupiter.api.Test;
import sae.semestre.six.HashUtils;
import sae.semestre.six.appointment.medicalact.MedicalAct;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class BillHashTest {

    @Test
    void testBillHash() {
        // Given two bills with the same initial state
        Bill bill1 = new Bill();
        Bill bill2 = new Bill();
        // Then that two new bills have the same hash code
        assertEquals(
                HashUtils.hashString(bill1.getInfoToHash()),
                HashUtils.hashString(bill2.getInfoToHash())
        );

        // When we modify the first bill
        bill1.setTotalAmount(100.0);
        // Then the hash code of the first bill is different from the second one
        assertNotEquals(
                HashUtils.hashString(bill1.getInfoToHash()),
                HashUtils.hashString(bill2.getInfoToHash())
        );

        // When we modify the second bill
        bill2.setTotalAmount(bill1.getTotalAmount());
        // Then the hash code of the first bill is equal to the second one
        assertEquals(
                HashUtils.hashString(bill1.getInfoToHash()),
                HashUtils.hashString(bill2.getInfoToHash())
        );

        // When we add a bill detail to the first bill
        bill1.addBillDetail(new BillDetail(new MedicalAct("Medical Act", 100.0), 1));
        // Then the hash code of the first bill is different from the second one
        assertNotEquals(
                HashUtils.hashString(bill1.getInfoToHash()),
                HashUtils.hashString(bill2.getInfoToHash())
        );

        // When we add the same bill detail to the second bill
        bill2.addBillDetail(new BillDetail(new MedicalAct("Medical Act", 100.0), 1));
        // Then the hash code of the first bill is equal to the second one
        assertEquals(
                HashUtils.hashString(bill1.getInfoToHash()),
                HashUtils.hashString(bill2.getInfoToHash())
        );
    }
}
