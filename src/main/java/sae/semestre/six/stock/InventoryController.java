package sae.semestre.six.stock;

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
public class InventoryController {
    
    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryRepository inventoryRepository;
    
    private final EmailService emailService = EmailService.getInstance();
    
    
    @PostMapping("/supplier-invoice")
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
    
    
    @GetMapping("/low-stock")
    public List<Inventory> getLowStockItems() {
        return inventoryService.findAll().stream()
            .filter(Inventory::needsRestock)
            .collect(Collectors.toList());
    }
    
    
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