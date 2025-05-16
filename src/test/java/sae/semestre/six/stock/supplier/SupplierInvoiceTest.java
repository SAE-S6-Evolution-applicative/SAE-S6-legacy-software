package sae.semestre.six.stock.supplier;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SupplierInvoiceTest {

    @Test
    void testGetDetails() {
        SupplierInvoice invoice = new SupplierInvoice();
        SupplierInvoiceDetail detail1 = new SupplierInvoiceDetail();
        SupplierInvoiceDetail detail2 = new SupplierInvoiceDetail();

        invoice.getDetails().add(detail1);
        invoice.getDetails().add(detail2);

        assertEquals(2, invoice.getDetails().size());
        assertTrue(invoice.getDetails().contains(detail1));
        assertTrue(invoice.getDetails().contains(detail2));
    }

    @Test
    void testSetDetails() {
        SupplierInvoice invoice = new SupplierInvoice();
        SupplierInvoiceDetail detail1 = new SupplierInvoiceDetail();
        SupplierInvoiceDetail detail2 = new SupplierInvoiceDetail();

        Set<SupplierInvoiceDetail> details = new HashSet<>();
        details.add(detail1);
        details.add(detail2);

        invoice.setDetails(details);

        assertEquals(2, invoice.getDetails().size());
        assertTrue(invoice.getDetails().contains(detail1));
        assertTrue(invoice.getDetails().contains(detail2));
    }
}
