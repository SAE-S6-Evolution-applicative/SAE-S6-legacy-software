/*
 * MedicineController.java                                 20 mai 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.prescription;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/medicines")
@Tag(name = "Medicine", description = "Medicine management API")
public class MedicineController {

    private final MedicineService medicineService;

    @Autowired
    public MedicineController(final MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @Operation(summary = "Get medicine by ID", description = "Retrieves a specific medicine by its unique identifier")
    @ApiResponse(responseCode = "200", description = "Medicine found successfully")
    @ApiResponse(responseCode = "404", description = "Medicine not found")
    @GetMapping("/{id}")
    public MedicineResponse getMedicineById(
            @Parameter(description = "Medicine ID") @PathVariable Long id) {
        return new MedicineResponse(medicineService.getMedicineById(id));
    }

    @Operation(summary = "Get all medicines", description = "Retrieves a list of all available medicines")
    @ApiResponse(responseCode = "200", description = "List of medicines retrieved successfully")
    @GetMapping
    public List<MedicineResponse> getAllMedicines() {
        return medicineService.getAllMedicines().stream()
                .map(MedicineResponse::new)
                .toList();
    }

    public record MedicineResponse(
            Long id,
            String name,
            double unitPrice
    ) {
        public MedicineResponse(Medicine medicine) {
            this(medicine.getId(), medicine.getName(), medicine.getUnitPrice());
        }
    }
}
