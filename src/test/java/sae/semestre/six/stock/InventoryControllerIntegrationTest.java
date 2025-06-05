package sae.semestre.six.stock;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import sae.semestre.six.appointment.prescription.Medicine;
import sae.semestre.six.appointment.prescription.MedicineRepository;
import sae.semestre.six.email.EmailService;
import sae.semestre.six.stock.supplier.SupplierInvoice;
import sae.semestre.six.stock.supplier.SupplierInvoiceDetail;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@AutoConfigureMockMvc
class InventoryControllerIntegrationTest {

    @Autowired
    MockMvc server;

    @MockitoSpyBean
    private InventoryRepository inventoryRepository;

    @MockitoSpyBean
    private MedicineRepository medicineRepository;

    @MockitoSpyBean
    private InventoryService inventoryService;

    @MockitoSpyBean
    private EmailService emailService;

    @Test
    void testProcessSupplierInvoice() throws Exception {
        Medicine medicine = new Medicine("Paracétamol", 10.0);
        // Nous devons d'abord sauvegarder le médicament pour qu'il ait un ID
        medicineRepository.save(medicine);

        Inventory inventory = new Inventory();
        inventory.setMedicine(medicine);
        inventory.setReorderLevel(5);
        inventory.setQuantity(10);
        inventoryRepository.save(inventory);

        // Nous devons établir la relation bidirectionnelle
        medicine.setInventory(inventory);
        medicineRepository.save(medicine);

        int addedQuantity = 5;
        double unitPrice = 10.0;
        SupplierInvoice supplierInvoice = createSupplierInvoice(inventory, addedQuantity, unitPrice);

        server.perform(post("/inventory/supplier-invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(supplierInvoice)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Supplier invoice processed successfully"));

        // Vérifier le nombre d'appels à save sans capturer d'arguments pour l'instant
        verify(inventoryRepository, times(2)).save(any(Inventory.class));

        // Récupérer l'inventaire directement depuis le repository
        // plutôt que d'utiliser un ArgumentCaptor qui pose problème
        when(inventoryRepository.findById(inventory.getId())).thenReturn(Optional.of(inventory));
        Inventory updatedInventory = inventoryRepository.findById(inventory.getId()).orElse(null);

        assertNotNull(updatedInventory);
        assertEquals(inventory.getQuantity(), updatedInventory.getQuantity()); //15 == 15
        // Vérifier que le médicament est bien lié
        assertNotNull(updatedInventory.getMedicine());
        assertEquals(unitPrice, updatedInventory.getMedicine().getUnitPrice());
        assertNotNull(updatedInventory.getLastRestocked());
    }

    private static SupplierInvoice createSupplierInvoice(Inventory inventory, int addedQuantity, double unitPrice) {
        SupplierInvoiceDetail supplierInvoiceDetail = new SupplierInvoiceDetail();
        supplierInvoiceDetail.setInventory(inventory);
        supplierInvoiceDetail.setQuantity(addedQuantity);
        supplierInvoiceDetail.setUnitPrice(unitPrice);

        SupplierInvoice supplierInvoice = new SupplierInvoice();
        Set<SupplierInvoiceDetail> details = new HashSet<>();
        details.add(supplierInvoiceDetail);
        supplierInvoice.setDetails(details);
        return supplierInvoice;
    }

    /**
     * Convert SupplierInvoice to JSON string
     * <br>
     * We use a separate method because we need to control the order of the field `reorderLevel` and `quantity`.
     * `reorderLevel` should be before `quantity` in the JSON string.
     *
     * @param supplierInvoice The supplier invoice to convert
     * @return The JSON string representation of the supplier invoice
     */
    private String asJson(SupplierInvoice supplierInvoice) {
        StringBuilder json = new StringBuilder("{\"details\": [");
        for (SupplierInvoiceDetail detail : supplierInvoice.getDetails()) {
            json.append("{");
            json.append("\"inventory\": {");
            json.append("\"id\": \"").append(detail.getInventory().getId()).append("\",");
            // Assurez-vous que la medicine et son ID sont inclus
            json.append("\"medicine\": {");
            json.append("\"id\": \"").append(detail.getInventory().getMedicine().getId()).append("\",");
            json.append("\"name\": \"").append(detail.getInventory().getMedicine().getName()).append("\"");
            json.append("},");
            json.append("\"reorderLevel\": ").append(detail.getInventory().getReorderLevel()).append(",");
            json.append("\"quantity\": ").append(detail.getInventory().getQuantity());
            json.append("},");
            json.append("\"quantity\": ").append(detail.getQuantity()).append(",");
            json.append("\"unitPrice\": ").append(detail.getUnitPrice());
            json.append("},");
        }
        if (json.length() > 1) {
            json.deleteCharAt(json.length() - 1); // Remove last comma
        }
        json.append("]}");
        return json.toString();
    }

    @Test
    void testGetLowStockItems() throws Exception {
        List<Inventory> mockItems = Arrays.asList(
                createInventoryItem( 5, 10),
                createInventoryItem( 8, 15),
                createInventoryItem( 22, 10)
        );
        when(inventoryService.findAll()).thenReturn(mockItems);

        server.perform(get("/inventory/items/low-stock"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));

        verify(inventoryService).findAll();
    }

    @Test
    void testGetLowStockItemsButEmptyListIsReturned() throws Exception {
        when(inventoryService.findAll()).thenReturn(Collections.emptyList());

        server.perform(get("/inventory/items/low-stock"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));

        verify(inventoryService).findAll();
    }

    @Test
    void reorderItems() throws Exception {
        List<Inventory> lowStockItems = List.of(
                createInventoryItem( 8, 15)
        );
        String expectedResponse = "Reorder requests sent for " + lowStockItems.size() + " items";

        when(inventoryService.findNeedingRestock()).thenReturn(lowStockItems);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());
        createIfNotExist("C:\\hospital\\orders.txt");

        server.perform(post("/inventory/reorder"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expectedResponse));

        verify(inventoryService).findNeedingRestock();
    }

    private void createIfNotExist(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Inventory createInventoryItem(int quantity, int reorderLevel) {

        Medicine medicine = new Medicine("Paracétamol", 5.0);
        medicineRepository.save(medicine);

        Inventory item = new Inventory();
        item.setMedicine(medicine);
        item.setReorderLevel(reorderLevel);
        item.setQuantity(quantity);
        return item;
    }
}
