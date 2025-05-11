package sae.semestre.six.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import sae.semestre.six.appointment.bill.BillingController;
import sae.semestre.six.appointment.bill.BillDao;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(BillingController.class)
class BillingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BillDao billDao;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(BillingController.getInstance()).build();
    }

    @Test
    void testProcessBill() throws Exception {
        mockMvc.perform(post("/billing/process")
                .param("patientId", "1")
                .param("doctorId", "1")
                .param("treatments", "CONSULTATION"))
                .andExpect(status().isOk());
    }

    @Test
    void testProcessBillWithInvalidPatient() throws Exception {
        mockMvc.perform(post("/billing/process")
                .param("patientId", "999") // Invalid patient ID
                .param("doctorId", "1")
                .param("treatments", "CONSULTATION"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Patient not found"));
    }

    @Test
    void testUpdatePrice() throws Exception {
        mockMvc.perform(put("/billing/price")
                .param("treatment", "CONSULTATION")
                .param("price", "75.0"))
                .andExpect(status().isOk())
                .andExpect(content().string("Price updated"));
    }

    @Test
    void testGetPrices() throws Exception {
        mockMvc.perform(get("/billing/prices"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"CONSULTATION\":50.0,\"XRAY\":150.0,\"CHIRURGIE\":1000.0}"));
    }

    @Test
    void testCalculateInsurance() throws Exception {
        mockMvc.perform(get("/billing/insurance")
                .param("amount", "1000"))
                .andExpect(status().isOk())
                .andExpect(content().string("Insurance coverage: $1000.0"));
    }
}
