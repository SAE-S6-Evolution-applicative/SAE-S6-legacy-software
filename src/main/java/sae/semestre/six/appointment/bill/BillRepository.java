package sae.semestre.six.appointment.bill;

import org.springframework.data.jpa.repository.JpaRepository;

interface BillRepository extends JpaRepository<Bill, Long> {
}
