/*
 * PatientHistoryService.java                                 15 mai 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.patient.history;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PatientHistoryService {

    private final PatientHistoryRepository patientHistoryRepository;

    @Autowired
    public PatientHistoryService(final PatientHistoryRepository patientHistoryRepository) {
        this.patientHistoryRepository = patientHistoryRepository;
    }

    /**
     * Searches for patient history records that match the provided keyword and fall within the specified date range.
     *
     * @param keyword The keyword to search for in patient history fields such as diagnosis, notes, or patient details.
     *                If null or empty, this criterion is ignored.
     * @param start   The start of the date range for the search (inclusive). If null, no lower date boundary is applied.
     * @param end     The end of the date range for the search (inclusive). If null, no upper date boundary is applied.
     * @return A list of patient history records that match the specified criteria. Returns an empty list if no records are found.
     */
    public List<PatientHistory> searchByMultipleCriteria(String keyword, LocalDateTime start, LocalDateTime end) {
        Specification<PatientHistory> spec = PatientHistorySpecification.searchByKeywordAndDateRange(keyword, start, end);
        return patientHistoryRepository.findAll(spec);
    }
}
