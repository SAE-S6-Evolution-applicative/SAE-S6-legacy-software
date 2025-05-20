/*
 * PatientHistoryController.java                                  19 mai. 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.patient.history;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/patients/history")
@Tag(name = "Patients history", description = "Patient management API")
public class PatientHistoryController {

    @Autowired
    private PatientHistoryRepository patientHistoryRepository;

    @Operation(summary = "Get history", description = "Retrieves all history records filtered with params")
    @ApiResponse(responseCode = "200", description = "Histories")
    @GetMapping("/search")
    public List<PatientHistory> searchHistory(
            @RequestParam String keyword,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return Collections.emptyList();
    }

    @Operation(summary = "Get a patient history", description = "Retrieves a patient history")
    @ApiResponse(responseCode = "200", description = "Patient histories summaries")
    @GetMapping("/{patientId}/summary")
    public Map<String, Object> getPatientSummary(@PathVariable Long patientId) {
        List<PatientHistory> histories = patientHistoryRepository.findAllByPatient_Id(patientId);

        Map<String, Object> summary = new HashMap<>();
        summary.put("visitCount", histories.size());
        
        double totalBilled = histories.stream()
                .mapToDouble(PatientHistory::getTotalBilledAmount)
                .sum();

        summary.put("totalBilled", totalBilled);
        return summary;
    }
} 