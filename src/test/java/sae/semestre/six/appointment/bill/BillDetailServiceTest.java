package sae.semestre.six.appointment.bill;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;
import sae.semestre.six.appointment.medicalact.MedicalActRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Transactional
class BillDetailServiceTest {

    @Autowired
    private BillDetailService billDetailService;
    @Autowired
    private MedicalActRepository medicalActRepository;
    @Autowired
    private BillRepository billRepository;
    @MockitoSpyBean
    private BillDetailRepository billDetailRepository;

}
