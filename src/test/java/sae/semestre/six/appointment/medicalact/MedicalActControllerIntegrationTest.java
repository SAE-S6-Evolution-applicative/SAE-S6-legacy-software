package sae.semestre.six.appointment.medicalact;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import sae.semestre.six.appointment.bill.Bill;
import sae.semestre.six.appointment.bill.BillDetail;
import sae.semestre.six.appointment.bill.BillDetailRepository;
import sae.semestre.six.appointment.bill.BillRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Transactional
@AutoConfigureMockMvc
class MedicalActControllerIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(MedicalActControllerIntegrationTest.class);

    @Autowired
    MockMvc server;

    @MockitoSpyBean
    private MedicalActService medicalActService;

    @MockitoSpyBean
    private MedicalActRepository medicalActRepository;

    @MockitoSpyBean
    private BillRepository billRepository;

    @MockitoSpyBean
    private BillDetailRepository billDetailRepository;

    @Test
    void testGetPrices() throws Exception {
        // Given some medicals act
        int initialCount = medicalActRepository.findAllByActive(true).size();
        var act1 = new MedicalAct("ACT1", 10.0);
        var act2 = new MedicalAct("ACT2", 20.0);
        var act3 = new MedicalAct("ACT3", 50.0);
        var act4 = new MedicalAct("ACT4", 60.0);
        var act5 = new MedicalAct("ACT5", 100.0);
        var act6 = new MedicalAct("ACT6", 5.0);

        var acts = List.of(act1, act2, act3, act4, act5, act6);

        medicalActRepository.saveAll(acts);

        // When we try to get all medicals act
        server.perform(get("/medicalAct/"))
                .andDo(result -> {
                    log.info("Result: {}", result.getResponse().getContentAsString());
                })
                // Then...
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.medicalActList").isArray())
                .andExpect(jsonPath("$.medicalActList.length()").value(acts.size() + initialCount))
                .andExpect(jsonPath("$.medicalActList[0].name").value(act1.getName()))
                .andExpect(jsonPath("$.medicalActList[0].price").value(act1.getPrice()))
                .andExpect(jsonPath("$.medicalActList[0].active").value(act1.isActive()));
    }

    @Test
    void testUpdatePrice() throws Exception {
        // Given a medical act
        var act1 = new MedicalAct("ACT1", 10.0);
        act1 = medicalActRepository.save(act1);

        Double updatedPrice = 75.0;

        // When we try to update the price of the medical act
        server.perform(put("/medicalAct/")
                        .param("idMedicalAct", act1.getId().toString())
                        .param("price", updatedPrice.toString()))
                // Then...
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // And then...
        assertFalse(medicalActRepository.findById(act1.getId()).orElseThrow().isActive());
        verify(medicalActService, times(1)).updatePrice(eq(updatedPrice), any(MedicalAct.class));
    }

    @Test
    void testUpdatePriceWithBill() throws Exception {
        // Given a bill with a medical act in their detail
        var act1 = new MedicalAct("ACT1", 10.0);
        act1 = medicalActRepository.save(act1);

        BillDetail billDetail = new BillDetail(act1, 1);
        Bill bill = billRepository.save(new Bill().addBillDetail(billDetail));
        assertEquals(act1.getPrice(), bill.getTotalAmount());

        Double updatedPrice = 75.0;

        // When we update the price of the medical act
        server.perform(put("/medicalAct/")
                        .param("idMedicalAct", act1.getId().toString())
                        .param("price", updatedPrice.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Then the bill has to be updated too
        assertFalse(medicalActRepository.findById(act1.getId()).orElseThrow().isActive());
        verify(medicalActService, times(1)).updatePrice(eq(updatedPrice), any(MedicalAct.class));
        assertEquals(act1.getPrice(), billRepository.findById(bill.getId()).orElseThrow().getTotalAmount());
    }

    @Test
    void testCreateMedicalAct() throws Exception {
        // Given parameters for a medical act
        String name = "ACT1";
        Double price = 100.0;
        String requestBody = """
                {
                    "name": "%s",
                    "price": %s
                }
                """.formatted(name, price);

        // When we try to create a medical act
        server.perform(post("/medicalAct/")
                        .contentType("application/json")
                        .content(requestBody))
                // Then...
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.medicalAct.price").value(price))
                .andExpect(jsonPath("$.medicalAct.name").value(name));
    }
}
