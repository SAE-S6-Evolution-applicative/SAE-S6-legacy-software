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

    /**
     * Retrieves all patient history records associated with the specified patient ID.
     *
     * @param patientId The unique identifier of the patient whose history records are to be retrieved.
     *                  Must not be null.
     * @return A list of {@link PatientHistory} objects associated with the given patient ID.
     *         Returns an empty list if no history records are found for the patient.
     */
    public List<PatientHistory> findAllByPatientId(Long patientId) {
        if (patientId == null) {
            throw new IllegalArgumentException("Patient ID cannot be null");
        }

        return patientHistoryRepository.findAllByPatient_Id(patientId);
    }

    /**
     * Generates a summary of a patient's medical history, including the total number of visits
     * and the cumulative billed amount.
     *
     * @param patientId The unique identifier of the patient whose summary is to be retrieved.
     *                  Must not be null.
     * @return A {@link PatientSummaryResponse} object containing the total number of visits
     *         and the cumulative billed amount for the specified patient.
     * @throws IllegalArgumentException if the provided patientId is null.
     */
    public PatientSummaryResponse getPatientSummary(Long patientId) {
        if (patientId == null) {
            throw new IllegalArgumentException("Patient ID cannot be null");
        }

        List<PatientHistory> patientHistories = findAllByPatientId(patientId);
        int visitCount = patientHistories.size();
        double totalBilledAmount = calculateTotalBilledAmount(patientHistories);

        return new PatientSummaryResponse(visitCount, totalBilledAmount);
    }

    /**
     * Calculates the total billed amount for a list of patient history records.
     *
     * @param histories The list of patient history records. Each record may contain associated
     *                  bills with their respective amounts.
     *                  Must not be null.
     * @return The cumulative total billed amount across all provided patient history records.
     *         If the list is empty, returns 0.0.
     */
    private double calculateTotalBilledAmount(List<PatientHistory> histories) {
        return histories.stream()
                .mapToDouble(PatientHistory::getTotalBilledAmount)
                .sum();
    }
}
