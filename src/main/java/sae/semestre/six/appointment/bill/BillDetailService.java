/*
 * BillDetailService.java                                  19 mai. 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.bill;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BillDetailService {

    private final BillDetailRepository billDetailRepository;

    @Autowired
    public BillDetailService(final BillDetailRepository billDetailRepository) {
        this.billDetailRepository = billDetailRepository;
    }
}
