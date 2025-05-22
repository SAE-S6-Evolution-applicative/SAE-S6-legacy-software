/*
 * MedicineControllerIntegrationTest.java                                 20 mai 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.prescription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import sae.semestre.six.exception.EntityNotFoundException;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@AutoConfigureMockMvc
class MedicineControllerIntegrationTest {

    @Autowired
    private MockMvc server;

    @MockitoBean
    private MedicineService medicineService;

    @MockitoBean
    private MedicineRepository medicineRepository;

    private Medicine medicine1;
    private Medicine medicine2;

    @BeforeEach
    void setUp() {
        // Create test medicines
        medicine1 = new Medicine("Paracetamol", 10.0);
        medicine1.setId(1L);
        medicine2 = new Medicine("Amoxicillin", 20.0);
        medicine2.setId(2L);
    }

    @Test
    void testGetMedicineById() throws Exception {
        // Given a medicine
        when(medicineService.getMedicineById(1L)).thenReturn(medicine1);

        // When we try to get the medicine by ID
        server.perform(get("/medicines/1"))
                .andDo(print())
                // Then we should get the medicine details
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(medicine1.getId()))
                .andExpect(jsonPath("$.name").value(medicine1.getName()))
                .andExpect(jsonPath("$.unitPrice").value(medicine1.getUnitPrice()));

        verify(medicineService).getMedicineById(1L);
    }

    @Test
    void testGetMedicineByIdNotFound() throws Exception {
        // Given a medicine doesn't exist
        when(medicineService.getMedicineById(999L))
                .thenThrow(new EntityNotFoundException("No medicine found with ID: 999"));

        // When we try to get the medicine by ID
        server.perform(get("/medicines/999"))
                .andDo(print())
                // Then we should get a 404 error
                .andExpect(status().isNotFound());

        verify(medicineService).getMedicineById(999L);
    }

    @Test
    void testGetAllMedicines() throws Exception {
        // Given we have a list of medicines
        List<Medicine> medicines = Arrays.asList(medicine1, medicine2);
        when(medicineService.getAllMedicines()).thenReturn(medicines);

        // When we try to get all medicines
        server.perform(get("/medicines"))
                .andDo(print())
                // Then we should get the list of medicines
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(medicine1.getId()))
                .andExpect(jsonPath("$[0].name").value(medicine1.getName()))
                .andExpect(jsonPath("$[0].unitPrice").value(medicine1.getUnitPrice()))
                .andExpect(jsonPath("$[1].id").value(medicine2.getId()))
                .andExpect(jsonPath("$[1].name").value(medicine2.getName()))
                .andExpect(jsonPath("$[1].unitPrice").value(medicine2.getUnitPrice()));

        verify(medicineService).getAllMedicines();
    }

    @Test
    void testGetAllMedicinesEmpty() throws Exception {
        // Given we have no medicines
        when(medicineService.getAllMedicines()).thenReturn(List.of());

        // When we try to get all medicines
        server.perform(get("/medicines"))
                .andDo(print())
                // Then we should get an empty list
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(medicineService).getAllMedicines();
    }
} 