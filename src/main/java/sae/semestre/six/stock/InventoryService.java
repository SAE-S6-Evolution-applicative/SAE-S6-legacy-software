/*
 * InventoryService.java                                 16 mai 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    /**
     * Updates the stock of an inventory item with the specified item code by
     * restocking it with the given quantity and saving the updated inventory
     * status to the repository.
     *
     * @param itemCode the unique code identifying the inventory item to update.
     * @param quantity the quantity to restock for the specified item.
     */
    public void updateStock(String itemCode, Integer quantity) {
        Inventory inventory = inventoryRepository.findByItemCode(itemCode);
        inventory.restock(quantity);
        inventoryRepository.save(inventory);

        System.out.println("Updated stock for " + itemCode + " to " + quantity);
    }

    /**
     * Updates the price of an inventory item identified by the specified item code.
     * The new price is saved in the repository after being set.
     *
     * @param itemCode the unique code identifying the inventory item whose price is to be updated.
     * @param price the new price to set for the specified inventory item.
     */
    public void updatePrice(String itemCode, Double price) {
        Inventory inventory = inventoryRepository.findByItemCode(itemCode);
        inventory.setUnitPrice(price);
        inventoryRepository.save(inventory);
    }

    /**
     * Retrieves an inventory item based on its unique item code.
     *
     * @param itemCode the unique code of the inventory item to be retrieved.
     * @return the {@code Inventory} object corresponding to the specified item code,
     *         or {@code null} if no such item exists.
     */
    public Inventory findByItemCode(String itemCode) {
        return inventoryRepository.findByItemCode(itemCode);
    }

    /**
     * Finds and retrieves a list of inventory items where the quantity is less
     * than the specified value.
     *
     * @param quantity the maximum quantity threshold for filtering inventory items.
     * @return a list of {@code Inventory} objects with a quantity less than the specified value.
     */
    public List<Inventory> findAllByQuantityLessThan(Integer quantity) {
        return inventoryRepository.findAllByQuantityLessThan(quantity);
    }

    /**
     * Finds and retrieves a list of inventory items that require restocking.
     * Items are considered to require restocking when their quantity is less than
     * or equal to the reorder level.
     *
     * @return a list of {@code Inventory} objects that need restocking.
     */
    public List<Inventory> findNeedingRestock() {
        return inventoryRepository.findNeedingRestock();
    }

    /**
     * Retrieves a list of all inventory items.
     *
     * @return a list of {@code Inventory} objects representing all inventory items in the repository.
     */
    public List<Inventory> findAll() {
        return inventoryRepository.findAll();
    }

}
