/*
 * PatientSummaryResponse.java                                 19 mai 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.patient.history;

/**
 * Represents a summary response containing patient visit and billing details.
 *
 * This record is used to encapsulate the essential summary data of a patient's history,
 * specifically the total number of visits and the cumulative billed amount.
 *
 * Fields:
 * - visitCount: The total number of visits made by the patient.
 * - totalBilled: The total amount billed for the patient's history.
 */
public record PatientSummaryResponse(long visitCount, double totalBilled) {
}
