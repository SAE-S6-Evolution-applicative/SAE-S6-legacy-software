package sae.semestre.six.appointment.bill;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

interface BillRepository extends JpaRepository<Bill, Long> {
    Bill findBillByBillNumber(String billNumber);

    List<Bill> findBillsByPatient_Id(Long patientId);

    List<Bill> findBillsByDoctor_Id(Long doctorId);

    List<Bill> findBillsByBillDateBetween(LocalDate startDate, LocalDate endDate);

    List<Bill> findBillsByStatus(String status);
}
