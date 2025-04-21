package sae.semestre.six.stock.supplier;

import org.junit.jupiter.api.Test;
import sae.semestre.six.stock.Inventory;

import static org.junit.jupiter.api.Assertions.*;

class SupplierInvoiceDetailTest {

    @Test
    void testGetAndSetId() {
        SupplierInvoiceDetail detail = new SupplierInvoiceDetail();
        Long id = 1L;
        detail.setId(id);
        assertEquals(id, detail.getId());
    }

    @Test
    void testGetAndSetSupplierInvoice() {
        SupplierInvoiceDetail detail = new SupplierInvoiceDetail();
        SupplierInvoice invoice = new SupplierInvoice();
        detail.setSupplierInvoice(invoice);
        assertEquals(invoice, detail.getSupplierInvoice());
    }

    @Test
    void testGetAndSetInventory() {
        SupplierInvoiceDetail detail = new SupplierInvoiceDetail();
        Inventory inventory = new Inventory();
        detail.setInventory(inventory);
        assertEquals(inventory, detail.getInventory());
    }

    @Test
    void testGetAndSetQuantity() {
        SupplierInvoiceDetail detail = new SupplierInvoiceDetail();
        Integer quantity = 10;
        detail.setQuantity(quantity);
        assertEquals(quantity, detail.getQuantity());
    }

    @Test
    void testGetAndSetUnitPrice() {
        SupplierInvoiceDetail detail = new SupplierInvoiceDetail();
        Double unitPrice = 100.0;
        detail.setUnitPrice(unitPrice);
        assertEquals(unitPrice, detail.getUnitPrice());
    }
}