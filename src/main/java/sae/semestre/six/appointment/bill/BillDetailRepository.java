package sae.semestre.six.appointment.bill;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sae.semestre.six.appointment.medicalact.MedicalAct;

import java.util.List;

@Repository
public interface BillDetailRepository extends JpaRepository<BillDetail, Long> {
    List<BillDetail> findAllByBill_StatusAndMedicalAct(Bill.Status status, MedicalAct medicalAct);
}