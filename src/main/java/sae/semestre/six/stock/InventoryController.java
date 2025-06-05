/*
 * InventoryController.java                                  19 mai. 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.stock;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sae.semestre.six.email.EmailService;
import sae.semestre.six.stock.supplier.SupplierInvoice;
import sae.semestre.six.stock.supplier.SupplierInvoiceDetail;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/inventory")
@Tag(name = "Inventory", description = "Inventory management API")
public class InventoryController {

    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);

    private final InventoryService inventoryService;

    private final InventoryRepository inventoryRepository;

    private final EmailService emailService;

    @Autowired
    public InventoryController(
            final InventoryService inventoryService,
            final InventoryRepository inventoryRepository,
            final EmailService emailService
    ) {
        this.inventoryService = inventoryService;
        this.inventoryRepository = inventoryRepository;
        this.emailService = emailService;
    }
    
    @Operation(summary = "Process a supplier invoice", description = "Processes a new supplier invoice")
    @ApiResponse(responseCode = "200", description = "Process completed")
    @PostMapping("/supplier-invoices")
    public String processSupplierInvoice(@RequestBody SupplierInvoice invoice) {
        try {
            for (SupplierInvoiceDetail detail : invoice.getDetails()) {
                Inventory inventory = detail.getInventory();
                
                inventory.setQuantity(inventory.getQuantity() + detail.getQuantity());
                inventory.setUnitPrice(detail.getUnitPrice());
                inventory.setLastRestocked(LocalDate.now());
                
                inventoryRepository.save(inventory);
            }

            return "Supplier invoice processed successfully";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Operation(summary = "Get inventory items with a low stock", description = "Retrieves all inventory items with a low stock")
    @ApiResponse(responseCode = "200", description = "Low stock items")
    @GetMapping("/items/low-stock")
    public List<Inventory> getLowStockItems() {
        return inventoryService.findAll().stream()
                .filter(Inventory::needsRestock)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Reorder items that need a restock", description = "Sends email requests to restock items that need a restock")
    @ApiResponse(responseCode = "200", description = "reorder completed")
    @PostMapping("/reorder")
    public String reorderItems() {
        List<Inventory> lowStockItems = inventoryService.findNeedingRestock();

        for (Inventory item : lowStockItems) {

            int reorderQuantity = item.getReorderLevel() * 2;

            logger.info("REORDER: {}, Quantity: {}", item.getItemCode(), reorderQuantity);

            emailService.sendEmail(
                    "supplier@example.com",
                    "Reorder Request",
                    "Please restock " + item.getName() + " (Quantity: " + reorderQuantity + ")"
            );
        }

        return "Reorder requests sent for " + lowStockItems.size() + " items";
    }
} 