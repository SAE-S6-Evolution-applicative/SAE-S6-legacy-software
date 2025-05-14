package sae.semestre.six.appointment.patient.history;

import sae.semestre.six.generic.AbstractHibernateDao;
import org.springframework.stereotype.Repository;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.*;
import java.time.LocalDate;

@Repository
public class PatientHistoryDaoImpl extends AbstractHibernateDao<PatientHistory, Long> implements PatientHistoryDao {
    
    @Override
    @SuppressWarnings("unchecked")
    public List<PatientHistory> findCompleteHistoryByPatientId(Long patientId) {
        
        return getEntityManager()
            .createQuery("SELECT DISTINCT ph FROM PatientHistory ph " +
                "JOIN ph.patient p " +
                "JOIN ph.appointments a " +
                "JOIN ph.prescriptions pr " +
                "JOIN ph.treatments t " +
                "JOIN ph.bills b " +
                "JOIN ph.labResults lr " +
                "WHERE p.id = :patientId " +
                "ORDER BY ph.visitDate DESC")
            .setParameter("patientId", patientId)
            .getResultList();
    }
    
    @Override
    public List<PatientHistory> searchByMultipleCriteria(String keyword, LocalDateTime startDate, LocalDateTime endDate) {
        String queryString = "SELECT ph FROM PatientHistory ph " +
                "LEFT JOIN ph.patient p " +
                "WHERE (UPPER(ph.diagnosis) LIKE :keyword OR UPPER(ph.notes) LIKE :keyword OR UPPER(p.firstName) LIKE :keyword OR UPPER(p.lastName) LIKE :keyword) " +
                "AND ph.visitDate BETWEEN :startDate AND :endDate";
        
        return getEntityManager().createQuery(queryString, PatientHistory.class)
                .setParameter("keyword", "%" + keyword.toUpperCase() + "%")
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }
} 