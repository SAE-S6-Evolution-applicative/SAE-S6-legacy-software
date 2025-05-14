package sae.semestre.six.appointment.medicalact;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sae.semestre.six.appointment.bill.BillService;
import sae.semestre.six.common.SuccessfullResponseModel;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/medicalAct")
public class MedicalActController {

    private MedicalActService medicalActService;
    private MedicalActRepository medicalActRepository;

    @Autowired
    public MedicalActController(MedicalActService medicalActService, MedicalActRepository medicalActRepository) {
        this.medicalActService = medicalActService;
        this.medicalActRepository = medicalActRepository;
    }

    @GetMapping("/")
    public PricesResponse getPrices() {
        return new PricesResponse(
                medicalActService.getAllActive()
        );
    }

    @PutMapping("/price")
    public SuccessfullResponseModel updatePrice(
            @RequestParam Long idMedicalAct,
            @RequestParam double price) throws Exception {
        MedicalAct medicalAct =  medicalActRepository.findById(idMedicalAct).orElseThrow();
        medicalActService.updatePrice(price,medicalAct);
        return new SuccessfullResponseModel("Price updated", true);
    }

    public record PricesResponse(List<MedicalAct> medicalActList) {
    }
}
