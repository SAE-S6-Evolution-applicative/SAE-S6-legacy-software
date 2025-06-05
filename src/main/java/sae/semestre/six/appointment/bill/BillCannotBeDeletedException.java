package sae.semestre.six.appointment.bill;

/**
 * Custom Exception thrown when a deletion is attempted on a bill.
 */
public class BillCannotBeDeletedException extends RuntimeException {
    /**
     * Constructs a new BillCannotBeDeletedException with the specified detail message.
     *
     * @param message the detail message
     */
    public BillCannotBeDeletedException(String message) {
        super(message);
    }
}
