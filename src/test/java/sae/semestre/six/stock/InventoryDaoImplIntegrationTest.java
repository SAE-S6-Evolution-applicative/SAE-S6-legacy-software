package sae.semestre.six.stock;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class InventoryDaoImplIntegrationTest {

    @Autowired
    private InventoryDao inventoryDao;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void testFindByItemCode() {
        // Given an item with a specific code
        final String ITEM_CODE = "TEST001";
        Inventory testItem = new Inventory();
        testItem.setItemCode(ITEM_CODE);
        testItem.setReorderLevel(10);
        testItem.setQuantity(50);
        testItem.setUnitPrice(25.99);
        testItem.setLastRestocked(new Date());

        // Store in the database
        entityManager.persist(testItem);
        entityManager.flush();

        // When we search for the item by code
        Inventory foundItem = inventoryDao.findByItemCode(ITEM_CODE);

        // Then the item should be found
        assertNotNull(foundItem, "The item should be found");
        assertEquals(testItem.getItemCode(), foundItem.getItemCode(), String.format("The item code should match with %s", ITEM_CODE));
        assertEquals(testItem.getQuantity(), foundItem.getQuantity(), "The quantity should match");
        assertEquals(testItem.getUnitPrice(), foundItem.getUnitPrice(), "The unit price should match");
    }

    @Test
    void testFindByItemCodeWithTwoEntity() {
        // Given two items with a specific code
        final String ITEM_CODE = "TEST001";
        Inventory testItem = new Inventory();
        testItem.setItemCode(ITEM_CODE);
        testItem.setReorderLevel(10);
        testItem.setQuantity(50);
        testItem.setUnitPrice(25.99);
        testItem.setLastRestocked(new Date());
        Inventory anotherInventory = new Inventory();
        anotherInventory.setItemCode("TEST002");

        // Store in the database
        entityManager.persist(testItem);
        entityManager.persist(anotherInventory);
        entityManager.flush();

        // When we search for the item by code
        Inventory foundItem = inventoryDao.findByItemCode(ITEM_CODE);

        // Then the item should be found
        assertNotNull(foundItem, "The item should be found");
        assertEquals(testItem.getItemCode(), foundItem.getItemCode(), String.format("The item code should match with %s", ITEM_CODE));
        assertEquals(testItem.getQuantity(), foundItem.getQuantity(), "The quantity should match");
        assertEquals(testItem.getUnitPrice(), foundItem.getUnitPrice(), "The unit price should match");
    }

    @Test
    void testFindByQuantityLessThan() {
        // Given an item with a specific quantity
        Inventory testItem = new Inventory();
        testItem.setItemCode("TEST001");
        testItem.setReorderLevel(10);
        final int QUANTITY = 5;
        testItem.setQuantity(QUANTITY);

        // Store in the database
        entityManager.persist(testItem);
        entityManager.flush();

        // When we search for items with QUANTITY less than QUANTITY + 1
        List<Inventory> foundItems = inventoryDao.findByQuantityLessThan(QUANTITY + 1);

        // Then the item should be found
        assertNotNull(foundItems, "The items should be found");
        assertEquals(1, foundItems.size(), "There should be one item found");

        Inventory firstItem = foundItems.getFirst();
        assertEquals(testItem.getItemCode(), firstItem.getItemCode(), String.format("The item code should match with %s", testItem.getItemCode()));
        assertEquals(testItem.getQuantity(), firstItem.getQuantity(), "The quantity should match");

        // When we search for items with QUANTITY less than QUANTITY
        foundItems = inventoryDao.findByQuantityLessThan(QUANTITY);

        // Then no items should be found
        assertNotNull(foundItems, "The items should be found");
        assertEquals(0, foundItems.size(), "There should be no item found");
    }

    @Test
    void testFindNeedingRestock() {
        // Given an item that need restock
        Inventory testItem = new Inventory();
        testItem.setItemCode("TEST001");
        testItem.setReorderLevel(10);
        testItem.setQuantity(5);
        assertTrue(testItem.needsRestock());

        // Store in the database
        entityManager.persist(testItem);
        entityManager.flush();

        // When we search for item that need restock
        List<Inventory> foudItems = inventoryDao.findNeedingRestock();

        // Then the list should contain the item
        assertNotNull(foudItems, "The items should be found");
        assertEquals(1, foudItems.size(), "There should be one item found");

        Inventory firstItem = foudItems.getFirst();
        assertEquals(testItem.getItemCode(), firstItem.getItemCode(), String.format("The item code should match with %s", testItem.getItemCode()));
        assertEquals(testItem.getQuantity(), firstItem.getQuantity(), "The quantity should match");
    }

    @Test
    void testUpdateStock() {
        // Given an item
        Inventory testItem = new Inventory();
        final String ITEM_CODE = "TEST001";
        final int REORDER_LEVEL = 10;
        final Date yesterday = Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant());
        testItem.setItemCode(ITEM_CODE);
        testItem.setReorderLevel(REORDER_LEVEL);
        testItem.setQuantity(5);
        testItem.setLastRestocked(yesterday);
        assertTrue(testItem.needsRestock());

        // Store in the database
        entityManager.persist(testItem);
        entityManager.flush();
        entityManager.detach(testItem);

        // When we restock the item
        inventoryDao.updateStock(ITEM_CODE, REORDER_LEVEL + 1);

        // Then the item in the DB should be updated
        Inventory updatedItem = inventoryDao.findByItemCode(ITEM_CODE);
        assertNotNull(updatedItem, "The item should be found");
        assertEquals(testItem.getItemCode(), updatedItem.getItemCode(), String.format("The item code should match with %s", testItem.getItemCode()));
        assertFalse(updatedItem.needsRestock(), "The item should not need restock");
        assertNotNull(updatedItem.getLastRestocked(), "The last restocked date should be updated");
        assertEquals(testItem.getReorderLevel(), updatedItem.getReorderLevel(), "The reorder level should be the same");

        assertEquals(REORDER_LEVEL + 1, updatedItem.getQuantity(), "The quantity should be updated");
        assertTrue(updatedItem.getLastRestocked().after(testItem.getLastRestocked()));

    }

    @Test
    void testUpdatePrice() {
        // Given an item
        Inventory testItem = new Inventory();
        final String ITEM_CODE = "TEST001";
        final double UNIT_PRICE = 25.99;
        testItem.setItemCode(ITEM_CODE);
        testItem.setUnitPrice(UNIT_PRICE);

        // Store in the database
        entityManager.persist(testItem);
        entityManager.flush();
        entityManager.detach(testItem);

        // When we update the price of the item
        final double NEW_UNIT_PRICE = 30.99;
        inventoryDao.updatePrice(ITEM_CODE, NEW_UNIT_PRICE);

        // Then the item in the DB should be updated
        Inventory updatedItem = inventoryDao.findByItemCode(ITEM_CODE);
        assertNotNull(updatedItem, "The item should be found");
        assertEquals(testItem.getItemCode(), updatedItem.getItemCode(), String.format("The item code should match with %s", testItem.getItemCode()));

        assertEquals(NEW_UNIT_PRICE, updatedItem.getUnitPrice(), "The unit price should be updated");
    }

}