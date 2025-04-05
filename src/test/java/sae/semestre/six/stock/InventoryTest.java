package sae.semestre.six.stock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp () {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown () {
        System.setOut(originalOut);
    }

    @Test
    void testNeedsRestock() {
        Inventory inventory = new Inventory();
        final int REORDER_LEVEL = 10;
        inventory.setReorderLevel(REORDER_LEVEL);

        inventory.setQuantity(0);
        assertTrue(inventory.needsRestock());
        inventory.setQuantity(REORDER_LEVEL);
        assertTrue(inventory.needsRestock());

        inventory.setQuantity(REORDER_LEVEL + 1);
        assertFalse(inventory.needsRestock());
        inventory.setQuantity(15);
        assertFalse(inventory.needsRestock());
    }

    @Test
    void testSetQuantity() {
        Inventory inventory = new Inventory();
        inventory.setReorderLevel(10);
        inventory.setQuantity(5);

        assertTrue(inventory.needsRestock());
        assertTrue(outContent.toString().contains("WARNING: Item") && outContent.toString().contains(" needs restock!"));
        originalOut.println(outContent);
    }

    @Test
    void testDecrementStock() {
        Inventory inventory = new Inventory();
        inventory.setReorderLevel(10);
        inventory.setQuantity(11);

        assertFalse(outContent.toString().contains("WARNING: Item") && outContent.toString().contains(" needs restock!"));
        inventory.decrementStock(1);
        assertTrue(outContent.toString().contains("WARNING: Item") && outContent.toString().contains(" needs restock!"));
        originalOut.println(outContent);
    }

    @Test
    void testDecrementStockWithItemCode() {
        Inventory inventory = new Inventory();
        final String ITEM_NAME = "Item Name";
        inventory.setReorderLevel(10);
        inventory.setQuantity(11);
        inventory.setItemCode(ITEM_NAME);

        final String LOG_EXPECTED = String.format("WARNING: Item %s needs restock!", ITEM_NAME);
        assertFalse(outContent.toString().contains(LOG_EXPECTED));
        inventory.decrementStock(1);
        originalOut.println(outContent);
        assertTrue(outContent.toString().contains(LOG_EXPECTED));
    }
}