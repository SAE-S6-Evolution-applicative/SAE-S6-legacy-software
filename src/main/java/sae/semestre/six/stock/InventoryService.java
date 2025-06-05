/*
 * InventoryService.java                                 16 mai 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sae.semestre.six.appointment.prescription.RefillMedicineRequest;
import sae.semestre.six.exception.EntityNotFoundException;

import java.util.List;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Autowired
    private InventoryService(final InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

//    /**
//     * Updates the stock of an inventory item with the specified item code by
//     * restocking it with the given quantity and saving the updated inventory
//     * status to the repository.
//     *
//     * @param itemCode the unique code identifying the inventory item to update.
//     * @param quantity the quantity to restock for the specified item.
//     */
//    public void updateStock(String itemCode, Integer quantity) {
//        Inventory inventory = inventoryRepository.findByItemCode(itemCode);
//        inventory.restock(quantity);
//        inventoryRepository.save(inventory);
//
//        System.out.println("Updated stock for " + itemCode + " to " + quantity);
//    }
//
//    /**
//     * Updates the price of an inventory item identified by the specified item code.
//     * The new price is saved in the repository after being set.
//     *
//     * @param itemCode the unique code identifying the inventory item whose price is to be updated.
//     * @param price    the new price to set for the specified inventory item.
//     */
//    public void updatePrice(String itemCode, Double price) {
//        Inventory inventory = inventoryRepository.findByItemCode(itemCode);
//        inventory.setUnitPrice(price);
//        inventoryRepository.save(inventory);
//    }

    /**
     * Finds and retrieves a {@code Medicine} object associated with the specified medicine ID.
     * If no matching medicine is found, an {@code IllegalArgumentException} will be thrown.
     *
     * @param medicineId the unique identifier of the medicine to be retrieved.
     * @return the {@code Medicine} object corresponding to the given medicine ID.
     * @throws IllegalArgumentException if no matching medicine is found with the specified ID.
     */
    public Inventory findByMedicine_Id(Long medicineId) {
        return inventoryRepository.findByMedicine_Id(medicineId).orElseThrow(
                () -> new EntityNotFoundException("No inventory line found with medicine ID : " + medicineId)
        );
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

    /**
     * Refills the stock of a specific medicine based on the details provided in the request.
     *
     * @param medicineRequest the request containing the details of the medicine to be refilled,
     *                        including the unique identifier of the medicine and the quantity to be added.
     */
    public void refillMedicine(RefillMedicineRequest medicineRequest) {
        Inventory inventory = findByMedicine_Id(medicineRequest.medicineId());

        inventory.restock(medicineRequest.quantity());
    }
}
