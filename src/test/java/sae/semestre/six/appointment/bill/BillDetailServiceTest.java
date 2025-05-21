package sae.semestre.six.appointment.bill;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;
import sae.semestre.six.appointment.medicalact.MedicalAct;
import sae.semestre.six.appointment.medicalact.MedicalActRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;

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
