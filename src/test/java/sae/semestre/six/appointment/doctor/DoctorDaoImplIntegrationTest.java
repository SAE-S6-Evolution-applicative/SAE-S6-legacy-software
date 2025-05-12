package sae.semestre.six.appointment.doctor;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class DoctorDaoImplIntegrationTest {

    @Autowired
    private DoctorDao doctorDao;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void testFindByDoctorNumber() {
        // Given
        Doctor doctor = new Doctor();
        doctor.setDoctorNumber("DOC001");
        doctor.setFirstName("John");
        doctor.setLastName("Smith");
        entityManager.persist(doctor);
        entityManager.flush();

        // When
        Doctor foundDoctor = doctorDao.findByDoctorNumber("DOC001");

        // Then
        assertNotNull(foundDoctor);
        assertEquals(doctor.getDoctorNumber(), foundDoctor.getDoctorNumber());
    }

    @Test
    void testFindBySpecialization() {
        // Given
        Doctor doctor = new Doctor();
        doctor.setSpecialization("Cardiology");
        doctor.setDoctorNumber("testDoctorNumber");
        doctor.setFirstName("John");
        doctor.setLastName("Smith");
        entityManager.persist(doctor);
        entityManager.flush();

        // When
        List<Doctor> doctors = doctorDao.findBySpecialization("Cardiology");

        // Then
        assertEquals(1, doctors.size());
        assertEquals(doctor.getId(), doctors.get(0).getId());
    }

}
