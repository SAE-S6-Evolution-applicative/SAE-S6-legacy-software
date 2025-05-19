package sae.semestre.six.stock;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sae.semestre.six.email.EmailService;
import sae.semestre.six.stock.supplier.SupplierInvoice;
import sae.semestre.six.stock.supplier.SupplierInvoiceDetail;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/inventory")
@Tag(name = "Inventory", description = "Inventory management API")
public class InventoryController {
    
    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryRepository inventoryRepository;
    
    private final EmailService emailService = EmailService.getInstance();

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
    @ApiResponse(responseCode = "200", description = "Low stocks items")
    @GetMapping("/items/low-stock")
    public List<Inventory> getLowStockItems() {
        return inventoryService.findAll().stream()
            .filter(Inventory::needsRestock)
            .collect(Collectors.toList());
    }

    @Operation(summary = "Reorder items who needs a restock", description = "Sends email requests to restock items who need a restock")
    @ApiResponse(responseCode = "200", description = "reorder completed")
    @PostMapping("/reorder")
    public String reorderItems() {
        List<Inventory> lowStockItems = inventoryService.findNeedingRestock();
        
        for (Inventory item : lowStockItems) {
            
            int reorderQuantity = item.getReorderLevel() * 2;
            
            
            try (FileWriter fw = new FileWriter("C:\\hospital\\orders.txt", true)) {
                fw.write("REORDER: " + item.getItemCode() + ", Quantity: " + reorderQuantity + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            
            emailService.sendEmail(
                "supplier@example.com",
                "Reorder Request",
                "Please restock " + item.getName() + " (Quantity: " + reorderQuantity + ")"
            );
        }
        
        return "Reorder requests sent for " + lowStockItems.size() + " items";
    }
} 