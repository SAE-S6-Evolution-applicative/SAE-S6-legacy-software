package sae.semestre.six.appointment.patient.history;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@AutoConfigureMockMvc
class PatientHistoryControllerTest {

    private MockMvc server;

    @MockitoBean
    private PatientHistoryService patientHistoryService;

    @InjectMocks
    private PatientHistoryController patientHistoryController;

    private AutoCloseable autoCloseable;

    private PatientHistory history1;

    private PatientHistory history2;

    @BeforeEach
    void setUp() throws ParseException {
        autoCloseable = MockitoAnnotations.openMocks(this);
        server = MockMvcBuilders.standaloneSetup(patientHistoryController).build();
        history1 = new PatientHistory();

        history1.setId(1L);
        history1.setVisitDate(LocalDateTime.of(2023, 5, 1, 1, 1));
        history1.setDiagnosis("Hello world!");
        history2 = new PatientHistory();
        history2.setId(2L);
        history2.setVisitDate(LocalDateTime.of(2024, 1, 1, 1, 1));
        history2.setDiagnosis("Hello world!");

    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testSearchHistory() throws Exception {
        String keyword = "hello";
        LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0,0);
        LocalDateTime endDate = LocalDateTime.of(2023, 12, 31, 0,0);

        server.perform(get("/patients/history/search")
                    .param("keyword", keyword)
                    .param("startDate",  startDate.toString())
                    .param("endDate", endDate.toString())
                    .contentType(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk());
    }
}