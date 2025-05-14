package sae.semestre.six.appointment.medicalact;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MedicalActController.class)
class MedicalActControllerIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(MedicalActControllerIntegrationTest.class);
    private MockMvc server;

    @MockitoBean
    private MedicalActService medicalActService;

    @InjectMocks
    private MedicalActController medicalActController;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        server = MockMvcBuilders.standaloneSetup(medicalActController).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testGetPrices() throws Exception {
        var act1 = new MedicalAct("ACT1", 10.0);
        var act2 = new MedicalAct("ACT2", 20.0);
        var act3 = new MedicalAct("ACT3", 50.0);
        var act4 = new MedicalAct("ACT4", 60.0);
        var act5 = new MedicalAct("ACT5", 100.0);
        var act6 = new MedicalAct("ACT6", 5.0);

        var acts = List.of(act1, act2, act3, act4, act5, act6);

        when(medicalActService.getAllActive()).thenReturn(acts);

        server.perform(get("/medicalAct/"))
                .andDo(result -> {
                    log.info("Result: {}", result.getResponse().getContentAsString());
                })
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.medicalActList").isArray())
                .andExpect(jsonPath("$.medicalActList.length()").value(acts.size()))
                .andExpect(jsonPath("$.medicalActList[0].name").value(act1.getName()))
                .andExpect(jsonPath("$.medicalActList[0].price").value(act1.getPrice()))
                .andExpect(jsonPath("$.medicalActList[0].active").value(act1.isActive()));
    }
}
