package sae.semestre.six.appointment.medicalact;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static sae.semestre.six.CustomAssertion.assertIsEmpty;

@SpringBootTest
class MedicalActServiceTest {

    @MockitoBean
    private MedicalActRepository medicalActRepository;

    @Autowired
    private MedicalActService medicalActService;

    @Test
    void findByIds_ShouldReturnMatchingMedicalActs_WhenIdsAreValid() {
        // Arrange
        MedicalAct act1 = new MedicalAct("Consultation", 50.0);
        MedicalAct act2 = new MedicalAct("Surgery", 200.0);
        setId(act1, 1L);
        setId(act2, 2L);

        List<Long> ids = Arrays.asList(1L, 2L);
        when(medicalActRepository.findAllById(ids)).thenReturn(Arrays.asList(act1, act2));

        // Act
        List<MedicalAct> result = medicalActService.findByIds(new Long[]{1L, 2L});

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.containsAll(Arrays.asList(act1, act2)) && result.size() == 2);
    }

    @Test
    void findByIds_ShouldThrow_WhenNoIdsMatch() {
        // Arrange
        List<Long> ids = Arrays.asList(3L, 4L);
        when(medicalActRepository.findAllById(ids)).thenReturn(Collections.emptyList());

        // Act
        assertThrows(IllegalArgumentException.class, () -> {
            medicalActService.findByIds(new Long[]{3L, 4L});
        });
    }

    @Test
    void findByIds_ShouldThrow_WhenOneIdDontMatch() {
        // Arrange
        MedicalAct act2 = new MedicalAct("Surgery", 200.0);
        setId(act2, 3L);
        List<Long> ids = Arrays.asList(act2.getId(), 4L);
        when(medicalActRepository.findAllById(ids)).thenReturn(List.of(act2));

        // Act
        assertThrows(IllegalArgumentException.class, () -> {
            medicalActService.findByIds(new Long[]{act2.getId(), 4L});
        });
    }

    @Test
    void findByIds_ShouldReturnEmptyList_WhenIdsArrayIsEmpty() {
        // Arrange
        when(medicalActRepository.findAllById(Collections.emptyList())).thenReturn(Collections.emptyList());

        // Act
        List<MedicalAct> result = medicalActService.findByIds(new Long[]{});

        // Assert
        assertIsEmpty(result);
    }


    private void setId(MedicalAct medicalAct, Long id) {
        ReflectionTestUtils.setField(medicalAct, "id", id);
    }
}