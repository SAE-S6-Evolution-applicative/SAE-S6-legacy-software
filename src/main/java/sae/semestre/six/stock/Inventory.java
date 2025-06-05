/*
 * Inventory.java                                  19 mai. 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.stock;

import jakarta.persistence.*;
import sae.semestre.six.appointment.prescription.Medicine;

import java.time.LocalDate;

@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "medicine_id", referencedColumnName = "id")
    private Medicine medicine;

    @Column(name = "quantity")
    private Integer quantity = 0;

    @Column(name = "reorder_level")
    private Integer reorderLevel;

    @Column(name = "last_restocked")
    private LocalDate lastRestocked;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity of the inventory. If the provided quantity is negative,
     * an {@link IllegalArgumentException} is thrown. If the quantity reaches or
     * falls below the reorder level, a warning message is displayed indicating
     * the item needs to be restocked.
     *
     * @param quantity the new quantity to set. Must be a non-negative integer.
     * @throws IllegalArgumentException if the quantity is less than 0.
     */
    public void setQuantity(Integer quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must be a positive number");
        }
        this.quantity = quantity;

        if (needsRestock()) {
            System.out.println("WARNING: Item " + medicine.getName() + " needs restock!");
        }
    }

    public Integer getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(Integer reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public LocalDate getLastRestocked() {
        return lastRestocked;
    }

    public void setLastRestocked(LocalDate lastRestocked) {
        this.lastRestocked = lastRestocked;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }

    /**
     * Checks if the inventory requires restocking. This is determined by comparing
     * the current quantity of the item to its reorder level. If the quantity is
     * less than or equal to the reorder level, the method returns true, indicating
     * that a restock is needed.
     *
     * @return true if the current quantity is less than or equal to the reorder level,
     *         false otherwise.
     */
    public boolean needsRestock() {
        return quantity <= reorderLevel;
    }

    /**
     * Decreases the stock quantity of this inventory by a specified amount.
     * This method adjusts the current quantity by subtracting the given amount.
     *
     * @param amount the amount to be decremented from the current quantity.
     *               Must be a non-negative integer.
     * @throws IllegalArgumentException if the resulting quantity becomes negative.
     */
    public void decrementStock(int amount) {
        this.setQuantity(this.getQuantity() - amount);
    }

    /**
     * Increases the quantity of the inventory by the specified amount.
     * If the provided amount is negative, the resulting quantity might decrease,
     * which is handled by the {@link Inventory#setQuantity} method to ensure
     * the inventory state remains consistent.
     *
     * @param amount the amount to increment the current quantity by.
     *               Must be a valid integer.
     */
    private void incrementQuantity(int amount) {
        this.setQuantity(this.getQuantity() + amount);
    }

    /**
     * Restocks the inventory by adding the specified quantity and updates
     * the last restocked date to the current date.
     *
     * @param quantity the number of items to add to the current inventory.
     *                 Must be a non-negative integer.
     */
    public void restock(Integer quantity) {
        this.incrementQuantity(quantity);
        this.setLastRestocked(LocalDate.now());
    }
}