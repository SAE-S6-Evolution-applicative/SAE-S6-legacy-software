package sae.semestre.six.appointment.bill;

public class BillCannotBeDeletedException extends RuntimeException {
    public BillCannotBeDeletedException(String message) {
        super(message);
    }
}
