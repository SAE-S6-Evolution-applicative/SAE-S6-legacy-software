package sae.semestre.six.appointment;

import org.springframework.stereotype.Repository;
import sae.semestre.six.generic.DaoHibernateAbstrait;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class RendezVousDaoImpl extends DaoHibernateAbstrait<Appointment, Long> implements DaoRendezVous {

    @Override
    @SuppressWarnings("unchecked")
    public List<Appointment> trouverParIdPatient(Long idPatient) {
        return getGestionnaireEntite()
                .createQuery("FROM Appointment WHERE patient.id = :idPatient")
                .setParameter("idPatient", idPatient)
                .getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Appointment> trouverParIdMedecin(Long idMedecin) {
        return getGestionnaireEntite()
                .createQuery("FROM Appointment WHERE doctor.id = :idMedecin")
                .setParameter("idMedecin", idMedecin)
                .getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Appointment> trouverParPlageDates(LocalDateTime dateDebut, LocalDateTime dateFin) {
        // Include appointments up to the end of dateFin day
        LocalDateTime finAjustee = dateFin.withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        return getGestionnaireEntite()
                .createQuery("FROM Appointment WHERE appointmentDate >= :dateDebut AND appointmentDate <= :dateFin")
                .setParameter("dateDebut", dateDebut)
                .setParameter("dateFin", finAjustee)
                .getResultList();
    }
} 
