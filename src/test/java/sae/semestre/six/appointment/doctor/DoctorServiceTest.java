/*
 * DoctorServiceTest.java                                 05 juin 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.doctor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sae.semestre.six.exception.EntityNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorService doctorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnDoctorById() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        Doctor found = doctorService.getDoctor(1L);

        assertEquals(1L, found.getId());
    }

    @Test
    void shouldThrowErrorWhenDoctorNotFound() {
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> doctorService.getDoctor(99L));
    }

}
