package sae.semestre.six.appointment.bill;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import sae.semestre.six.appointment.doctor.DoctorRepository;
import sae.semestre.six.appointment.patient.PatientRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BillController.class)
class BillControllerIntegrationTest {

    private MockMvc server;

    @MockitoBean
    private BillRepository billRepository;

    @MockitoBean
    private PatientRepository patientRepository;

    @MockitoBean
    private DoctorRepository doctorRepository;

    @InjectMocks
    private BillController billController;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        server = MockMvcBuilders.standaloneSetup(billController).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testProcessBill() throws Exception {
        server.perform(post("/bills")
                .param("patientId", "1")
                .param("doctorId", "1")
                .param("treatments", "CONSULTATION"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdatePrice() throws Exception {
        server.perform(put("/bills/price")
                .param("treatment", "CONSULTATION")
                .param("price", "75.0"))
                .andExpect(status().isOk())
                .andExpect(content().string("Price updated"));
    }

    @Test
    void testGetPrices() throws Exception {
        server.perform(get("/bills/prices"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"CONSULTATION\":50.0,\"XRAY\":150.0,\"CHIRURGIE\":1000.0}"));
    }

    @Test
    void testCalculateInsurance() throws Exception {
        server.perform(get("/bills/insurance-coverage")
                .param("amount", "1000"))
                .andExpect(status().isOk())
                .andExpect(content().string("Insurance coverage: $1000.0"));
    }

    @Test
    void testUpdatePriceWithInvalidTreatment() throws Exception {
        server.perform(put("/bills/price")
                        .param("treatment", "INVALID_TREATMENT")
                        .param("price", "100.0"))
                .andExpect(status().isOk())
                .andExpect(content().string("Price updated"));
    }

    @Test
    void testGetTotalRevenue() throws Exception {
        server.perform(get("/bills/revenue"))
                .andExpect(status().isOk())
                .andExpect(content().string("Total Revenue: $0.0"));
    }

    @Test
    void testGetPendingBills() throws Exception {
        server.perform(get("/bills/pending"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));
    }


    @Test
    void testCalculateInsuranceWithZeroAmount() throws Exception {
        server.perform(get("/bills/insurance-coverage")
                        .param("amount", "0"))
                .andExpect(status().isOk())
                .andExpect(content().string("Insurance coverage: $0.0"));
    }
}
