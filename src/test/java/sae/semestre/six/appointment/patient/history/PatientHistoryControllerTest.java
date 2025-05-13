package sae.semestre.six.appointment.patient.history;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import sae.semestre.six.appointment.patient.PatientDao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(PatientHistoryController.class)
class PatientHistoryControllerTest {
    private MockMvc server;

    @MockitoBean
    private PatientHistoryDao patientHistoryDao;

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

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        history1.setId(1L);
        history1.setVisitDate(dateFormat
                .parse("2023-05-01"));
        history1.setDiagnosis("Hello world!");
        history2 = new PatientHistory();
        history2.setId(2L);
        history2.setVisitDate(dateFormat
                .parse("2024-01-01"));
        history2.setDiagnosis("Hello world!");

    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testSearchHistory() throws Exception {
        String keyword = "hello";
        String startDate = "2023-01-01";
        String endDate = "2023-12-31";

        server.perform(get("/patient-history/search")
                    .param("keyword", keyword)
                    .param("startDate",  startDate)
                    .param("endDate", endDate)
                    .contentType(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk());
    }
}