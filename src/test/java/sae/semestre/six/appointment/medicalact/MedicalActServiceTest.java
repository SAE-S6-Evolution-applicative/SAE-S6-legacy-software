package sae.semestre.six.appointment.medicalact;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static sae.semestre.six.CustomAssertion.assertIsEmpty;

@SpringBootTest
@Transactional
class MedicalActServiceTest {

    @MockitoBean
    private MedicalActRepository medicalActRepository;

    @Autowired
    private MedicalActService medicalActService;

    @Test
    void findByIds_ShouldReturnMatchingMedicalActs_WhenIdsAreValid() {
        // Given medicals act
        MedicalAct act1 = new MedicalAct("Consultation", 50.0);
        MedicalAct act2 = new MedicalAct("Surgery", 200.0);
        setId(act1, 1L);
        setId(act2, 2L);
        List<Long> ids = Arrays.asList(1L, 2L);

        // When we find by ids the medicals a
        when(medicalActRepository.findAllById(ids)).thenReturn(Arrays.asList(act1, act2));
        List<MedicalAct> result = medicalActService.findByIds(new Long[]{1L, 2L});

        // Then...
        assertEquals(2, result.size());
        assertTrue(result.containsAll(Arrays.asList(act1, act2)) && result.size() == 2);
    }

    @Test
    void findByIds_ShouldThrow_WhenNoIdsMatch() {
        // Given a list of wrong medical act ids
        List<Long> ids = Arrays.asList(3L, 4L);

        // When we try to find medicals act with wrong ids
        when(medicalActRepository.findAllById(ids)).thenReturn(Collections.emptyList());

        // Then...
        assertThrows(IllegalArgumentException.class, () -> {
            medicalActService.findByIds(new Long[]{3L, 4L});
        });
    }

    @Test
    void findByIds_ShouldThrow_WhenOneIdDontMatch() {
        // Given a list of medical act ids with one wrong id
        MedicalAct act2 = new MedicalAct("Surgery", 200.0);
        setId(act2, 3L);
        List<Long> ids = Arrays.asList(act2.getId(), 4L);

        // When we try to find medicals act
        when(medicalActRepository.findAllById(ids)).thenReturn(List.of(act2));

        // Then...
        assertThrows(IllegalArgumentException.class, () -> medicalActService.findByIds(new Long[]{act2.getId(), 4L}));
    }

    @Test
    void findByIds_ShouldReturnEmptyList_WhenIdsArrayIsEmpty() {
        // Given an empty list of medical act id
        // When we try to find medical act by id with an empty list of id
        when(medicalActRepository.findAllById(Collections.emptyList())).thenReturn(Collections.emptyList());
        List<MedicalAct> result = medicalActService.findByIds(new Long[]{});

        // Then...
        assertIsEmpty(result);
    }


    private void setId(MedicalAct medicalAct, Long id) {
        ReflectionTestUtils.setField(medicalAct, "id", id);
    }
}