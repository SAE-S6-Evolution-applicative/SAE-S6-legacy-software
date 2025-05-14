/*
 * RendezVousDaoImplTest.java                                 13 mai 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.appointment;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import sae.semestre.six.appointment.Appointment;
import sae.semestre.six.appointment.RendezVousDaoImpl;
import sae.semestre.six.appointment.doctor.Doctor;
import sae.semestre.six.appointment.patient.Patient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class RendezVousDaoImplTest {

    @Autowired
    private RendezVousDaoImpl rendezVousDaoImpl;

    @PersistenceContext
    private EntityManager entityManager;

    private Patient patient1;
    private Patient patient2;
    private Doctor medecin1;
    private Doctor medecin2;
    private LocalDateTime aujourdHui;
    private LocalDateTime hier;
    private LocalDateTime demain;
    private LocalDateTime semaineSuivante;

    @BeforeEach
    void setUp() {
        // Préparation des dates
        aujourdHui = LocalDateTime.now();
        hier = aujourdHui.minusDays(1);
        demain = aujourdHui.plusDays(1);
        semaineSuivante = aujourdHui.plusDays(7);

        // Création des patients
        patient1 = new Patient();
        patient1.setFirstName("John");
        patient1.setLastName("Doe");
        patient1.setPatientNumber("PAT001");
        entityManager.persist(patient1);

        patient2 = new Patient();
        patient2.setFirstName("Jane");
        patient2.setLastName("Smith");
        patient2.setPatientNumber("PAT002");
        entityManager.persist(patient2);

        // Création des médecins
        medecin1 = new Doctor();
        medecin1.setFirstName("Dr.");
        medecin1.setLastName("House");
        medecin1.setDoctorNumber("DOC001");
        entityManager.persist(medecin1);

        medecin2 = new Doctor();
        medecin2.setFirstName("Dr.");
        medecin2.setLastName("Wilson");
        medecin2.setDoctorNumber("DOC002");
        entityManager.persist(medecin2);

        entityManager.flush();
    }

    /**
     * Méthode utilitaire pour créer un rendez-vous
     */
    private Appointment creerRendezVous(Patient patient, Doctor medecin, LocalDateTime date, String numeroRendezVous) {
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(medecin);
        appointment.setAppointmentDate(date);
        appointment.setAppointmentNumber(numeroRendezVous);
        appointment.setStatus("Programmé");
        entityManager.persist(appointment);
        return appointment;
    }

    @Test
    void testTrouverParIdPatient() {
        // Given
        Appointment appointment1 = creerRendezVous(patient1, medecin1, aujourdHui, "APP001");
        Appointment appointment2 = creerRendezVous(patient1, medecin2, demain, "APP002");
        Appointment appointment3 = creerRendezVous(patient2, medecin1, aujourdHui, "APP003");
        entityManager.flush();

        // When
        List<Appointment> rendezvousPatient1 = rendezVousDaoImpl.trouverParIdPatient(patient1.getId());
        List<Appointment> rendezvousPatient2 = rendezVousDaoImpl.trouverParIdPatient(patient2.getId());

        // Then
        assertEquals(2, rendezvousPatient1.size());
        assertEquals(1, rendezvousPatient2.size());

        assertTrue(rendezvousPatient1.stream()
                .anyMatch(a -> a.getAppointmentNumber().equals("APP001")));
        assertTrue(rendezvousPatient1.stream()
                .anyMatch(a -> a.getAppointmentNumber().equals("APP002")));
        assertTrue(rendezvousPatient2.stream()
                .anyMatch(a -> a.getAppointmentNumber().equals("APP003")));
    }

    @Test
    void testTrouverParIdMedecin() {
        // Given
        Appointment appointment1 = creerRendezVous(patient1, medecin1, aujourdHui, "APP004");
        Appointment appointment2 = creerRendezVous(patient2, medecin1, demain, "APP005");
        Appointment appointment3 = creerRendezVous(patient1, medecin2, aujourdHui, "APP006");
        entityManager.flush();

        // When
        List<Appointment> rendezVousMedecin1 = rendezVousDaoImpl.trouverParIdMedecin(medecin1.getId());
        List<Appointment> rendezVousMedecin2 = rendezVousDaoImpl.trouverParIdMedecin(medecin2.getId());

        // Then
        assertEquals(2, rendezVousMedecin1.size());
        assertEquals(1, rendezVousMedecin2.size());

        assertTrue(rendezVousMedecin1.stream()
                .anyMatch(a -> a.getAppointmentNumber().equals("APP004")));
        assertTrue(rendezVousMedecin1.stream()
                .anyMatch(a -> a.getAppointmentNumber().equals("APP005")));
        assertTrue(rendezVousMedecin2.stream()
                .anyMatch(a -> a.getAppointmentNumber().equals("APP006")));
    }

    @Test
    void testTrouverParPlageDates() {
        // Given
        Appointment appointment1 = creerRendezVous(patient1, medecin1, hier, "APP007");
        Appointment appointment2 = creerRendezVous(patient1, medecin2, aujourdHui, "APP008");
        Appointment appointment3 = creerRendezVous(patient2, medecin1, demain, "APP009");
        Appointment appointment4 = creerRendezVous(patient2, medecin2, semaineSuivante, "APP010");
        entityManager.flush();

        // When
        List<Appointment> rendezVousActuels = rendezVousDaoImpl.trouverParPlageDates(hier, demain);
        List<Appointment> rendezVousFuturs = rendezVousDaoImpl.trouverParPlageDates(demain, semaineSuivante);

        // Then
        assertEquals(3, rendezVousActuels.size());
        assertEquals(2, rendezVousFuturs.size());

        assertTrue(rendezVousActuels.stream()
                .anyMatch(a -> a.getAppointmentNumber().equals("APP007")));
        assertTrue(rendezVousActuels.stream()
                .anyMatch(a -> a.getAppointmentNumber().equals("APP008")));
        assertTrue(rendezVousActuels.stream()
                .anyMatch(a -> a.getAppointmentNumber().equals("APP009")));

        assertTrue(rendezVousFuturs.stream()
                .anyMatch(a -> a.getAppointmentNumber().equals("APP009")));
        assertTrue(rendezVousFuturs.stream()
                .anyMatch(a -> a.getAppointmentNumber().equals("APP010")));
    }
}
