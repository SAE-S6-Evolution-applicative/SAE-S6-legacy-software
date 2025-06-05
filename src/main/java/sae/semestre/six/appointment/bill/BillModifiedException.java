package sae.semestre.six.appointment.bill;

/**
 * Custom Exception thrown when an update is attempted on a bill's protected fields
 */
public class BillModifiedException extends RuntimeException {
    public BillModifiedException(String message) {
        super(message);
    }
}
