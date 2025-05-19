/*
 * PatientHistorySpecification.java                                 15 mai 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.patient.history;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import sae.semestre.six.appointment.patient.Patient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PatientHistorySpecification {

    /**
     * Creates a specification for searching patient history by keyword and date range
     */
    public static Specification<PatientHistory> searchByKeywordAndDateRange(String keyword, LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.trim().isEmpty()) {
                predicates.add(createKeywordPredicate(keyword, root, cb));
            }

            if (startDate != null && endDate != null) {
                predicates.add(createDateRangePredicate(startDate, endDate, root, cb));
            }

            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Creates a predicate for keyword search across multiple fields
     */
    private static Predicate createKeywordPredicate(String keyword, Root<PatientHistory> root, CriteriaBuilder cb) {
        String likePattern = "%" + keyword.toUpperCase() + "%";
        Join<PatientHistory, Patient> patient = root.join("patient", JoinType.LEFT);

        return cb.or(
                createStringMatchPredicate(root, "diagnosis", likePattern, cb),
                createStringMatchPredicate(root, "notes", likePattern, cb),
                createStringMatchPredicate(patient, "firstName", likePattern, cb),
                createStringMatchPredicate(patient, "lastName", likePattern, cb)
        );
    }

    /**
     * Creates a predicate for date range filtering
     */
    private static Predicate createDateRangePredicate(LocalDateTime startDate, LocalDateTime endDate,
                                                      Root<PatientHistory> root, CriteriaBuilder cb) {
        return cb.between(root.get("visitDate"), startDate, endDate);
    }

    /**
     * Creates a case-insensitive LIKE predicate for a string field
     */
    private static <T> Predicate createStringMatchPredicate(From<?, T> from, String fieldName,
                                                            String likePattern, CriteriaBuilder cb) {
        return cb.like(cb.upper(from.get(fieldName)), likePattern);
    }

    /**
     * Creates a specification that filters patient history records containing the given keyword
     * in diagnosis, notes, or patient's first/last name.
     *
     * @param keyword The search term to look for (case-insensitive)
     * @return A specification that matches patient history containing the keyword, or all records if keyword is empty
     */
    public static Specification<PatientHistory> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return cb.conjunction();
            }
            return createKeywordPredicate(keyword, root, cb);
        };
    }

    /**
     * Creates a specification that filters patient history records within a given date range.
     *
     * @param startDate The beginning of the date range (inclusive)
     * @param endDate   The end of the date range (inclusive)
     * @return A specification that matches records within the date range, or all records if either date is null
     */
    public static Specification<PatientHistory> inDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, cb) -> {
            if (startDate == null || endDate == null) {
                return cb.conjunction();
            }
            return createDateRangePredicate(startDate, endDate, root, cb);
        };
    }
}
