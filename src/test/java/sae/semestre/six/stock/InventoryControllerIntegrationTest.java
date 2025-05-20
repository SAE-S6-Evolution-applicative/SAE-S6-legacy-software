package sae.semestre.six.stock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
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
    private InventoryService inventoryService;

    @MockitoSpyBean
    private EmailService emailService;

    @Test
    void testProcessSupplierInvoice() throws Exception {
        Inventory inventory = new Inventory();
        inventory.setItemCode("ITEM001");
        inventory.setReorderLevel(5);
        inventory.setQuantity(10);
        int addedQuantity = 5;
        double unitPrice = 10.0;
        SupplierInvoice supplierInvoice = createSupplierInvoice(inventory, addedQuantity, unitPrice);

        server.perform(post("/inventory/supplier-invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(supplierInvoice)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Supplier invoice processed successfully"));

        ArgumentCaptor<Inventory> inventoryCaptor = ArgumentCaptor.forClass(Inventory.class);
        verify(inventoryRepository).save(inventoryCaptor.capture());

        Inventory updatedInventory = inventoryCaptor.getValue();
        assertEquals(inventory.getQuantity() + addedQuantity, updatedInventory.getQuantity());
        assertEquals(unitPrice, updatedInventory.getUnitPrice());
        assertNotNull(updatedInventory.getLastRestocked());
    }

    @Test
    void testProcessSupplierInvoiceButAnExceptionOccurWhenSaving() throws Exception {
        Inventory inventory = new Inventory();
        inventory.setItemCode("ITEM001");
        inventory.setReorderLevel(5);
        inventory.setQuantity(10);
        int addedQuantity = 5;
        double unitPrice = 10.0;
        SupplierInvoice supplierInvoice = createSupplierInvoice(inventory, addedQuantity, unitPrice);

        String errorMessage = "Database error";
        doThrow(new RuntimeException(errorMessage)).when(inventoryRepository).save(any());

        server.perform(post("/inventory/supplier-invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(supplierInvoice)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Error: " + errorMessage));
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
            json.append("\"itemCode\": \"").append(detail.getInventory().getItemCode()).append("\",");
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
                createInventoryItem("ITEM001", 5, 10),
                createInventoryItem("ITEM002", 8, 15),
                createInventoryItem("ITEM003", 22, 10)
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
                createInventoryItem("ITEM002", 8, 15)
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

    private Inventory createInventoryItem(String code, int quantity, int reorderLevel) {
        Inventory item = new Inventory();
        item.setItemCode(code);
        item.setName("Test Item " + code);
        item.setReorderLevel(reorderLevel);
        item.setQuantity(quantity);
        return item;
    }
}
