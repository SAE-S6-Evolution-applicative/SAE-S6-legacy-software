package sae.semestre.six.stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Inventory findByItemCode(String itemCode);

    List<Inventory> findAllByQuantityLessThan(Integer quantity);

    @Query("SELECT i FROM Inventory i WHERE i.quantity <= i.reorderLevel")
    List<Inventory> findNeedingRestock();

    List<Inventory> findAll();
} 