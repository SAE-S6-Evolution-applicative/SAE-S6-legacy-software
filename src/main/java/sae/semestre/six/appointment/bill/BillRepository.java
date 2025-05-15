package sae.semestre.six.appointment.bill;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

interface BillRepository extends JpaRepository<Bill, Long> {
    Bill findBillByBillNumber(String billNumber);

    List<Bill> findBillsByPatient_Id(Long patientId);

    List<Bill> findBillsByDoctor_Id(Long doctorId);

    List<Bill> findBillsByBillDateBetween(LocalDate startDate, LocalDate endDate);

    List<Bill> findBillsByStatus(Bill.Status status);

    @Query("""
            SELECT SUM(b.totalAmount) FROM Bill b
            """)
    Double findTotalRevenue();
}
