/*
 * RefillMedicineRequest.java                                 21 mai 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.prescription;

/**
 * A record representing a request to refill a specific medicine.
 * This provides a concise way to encapsulate the data necessary for refilling
 * a medicine, including its identifier and the requested quantity.
 *
 * This class is immutable and used to pass the refill request information
 * consistently across different layers in the application.
 *
 * Fields:
 * - medicineId: The unique identifier of the medicine to be refilled.
 * - quantity: The quantity of the medicine being requested for refill.
 */
public record RefillMedicineRequest(Long medicineId, int quantity) {
}
