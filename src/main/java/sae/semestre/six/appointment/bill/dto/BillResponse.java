/*
 * BillDTO.java                                  05 juin 2025
 * IUT de Rodez, pas de droit d'auteur
 */

package sae.semestre.six.appointment.bill.dto;

import sae.semestre.six.appointment.bill.Bill;

import java.time.LocalDateTime;
import java.util.List;

public record BillResponse(
        Long id,
        LocalDateTime creationDate,
        String billNumber,
        Bill.Status status,
        Double totalAmount,
        String patientName,
        String doctorName
) {

    public BillResponse(Bill bill) {
        this(
                bill.getId(),
                bill.getBillDate(),
                bill.getBillNumber(),
                bill.getStatus(),
                bill.getTotalAmount(),
                bill.getPatient().getFirstName() + " " + bill.getPatient().getLastName(),
                bill.getDoctor().getFirstName() + " " + bill.getDoctor().getLastName()
        );
    }
}
