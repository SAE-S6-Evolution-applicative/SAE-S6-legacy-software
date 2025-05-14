package sae.semestre.six.appointment.bill;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sae.semestre.six.appointment.medicalact.MedicalAct;

import java.util.List;

@Repository
public interface BillDetailRepository extends JpaRepository<BillDetail, Long> {
    @Query("""
            SELECT bd FROM BillDetail bd
            WHERE bd.bill.status = sae.semestre.six.appointment.bill.Bill.Status.PENDING
            AND bd.medicalAct = :medicalAct
            """)
    List<BillDetail> findAllByPendingBillAndMedicalAct(MedicalAct medicalAct);
}