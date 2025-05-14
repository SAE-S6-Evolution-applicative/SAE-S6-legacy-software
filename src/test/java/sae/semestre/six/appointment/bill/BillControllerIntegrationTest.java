package sae.semestre.six.appointment.bill;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import sae.semestre.six.appointment.Appointment;
import sae.semestre.six.appointment.doctor.Doctor;
import sae.semestre.six.appointment.doctor.DoctorDao;
import sae.semestre.six.appointment.patient.PatientDao;
import sae.semestre.six.appointment.doctor.DoctorRepository;
import sae.semestre.six.appointment.patient.PatientRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

        Appointment appointment = new Appointment();

        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setAppointments(Set.of(appointment));

        when(doctorDao.findById(doctor.getId())).thenReturn(doctor);

        server.perform(post("/bills")
                        .param("patientId", "1")
                        .param("doctorId", "1")
                        .param("treatments", "CONSULTATION"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdatePrice() throws Exception {
        String consultation = "CONSULTATION";
        String updatedPrice = "75.0";
        server.perform(put("/bills/price")
                        .param("treatment", consultation)
                        .param("price", updatedPrice))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        server.perform(get("/billing/prices"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.prices." + consultation).value(updatedPrice));
    }

    @Test
    void testGetPrices() throws Exception {
        server.perform(get("/bills/prices"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.prices." + treatments.get(0)).value(prices.get(treatments.get(0))));
    }

    @Test
    void testCalculateInsurance() throws Exception {
        String amount = "1000.0";
        server.perform(get("/bills/insurance-coverage")
                        .param("amount", amount))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(amount));
    }

    @Test
    void testUpdatePriceWithNewTreatment() throws Exception {
        String newTreatment = "NEW_TREATMENT";
        Double price = 100.0;
        server.perform(put("/bills/price")
                        .param("treatment", newTreatment)
                        .param("price", price.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        @SuppressWarnings("unchecked")
        Map<String, Double> prices = (Map<String, Double>) ReflectionTestUtils.getField(billController, "priceList");
        assertEquals(price, prices.get(newTreatment));
    }

    @Test
    void testGetTotalRevenue() throws Exception {
        server.perform(get("/bills/revenue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRevenue").value(0.0));
    }

    @Test
    void testGetPendingBills() throws Exception {
        server.perform(get("/bills/pending"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pendingBills").isEmpty());
    }


    @Test
    void testCalculateInsuranceWithZeroAmount() throws Exception {
        String amount = "0.0";
        server.perform(get("/bills/insurance-coverage")
                        .param("amount", amount))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(amount));
    }
}
