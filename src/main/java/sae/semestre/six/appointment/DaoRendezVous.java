package sae.semestre.six.appointment;

import sae.semestre.six.generic.DaoGenerique;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DaoRendezVous extends DaoGenerique<Appointment, Long> {
    List<Appointment> trouverParIdPatient(Long idPatient);
    List<Appointment> trouverParIdMedecin(Long idMedecin);
    List<Appointment> trouverParPlageDates(LocalDateTime dateDebut, LocalDateTime dateFin);
} 