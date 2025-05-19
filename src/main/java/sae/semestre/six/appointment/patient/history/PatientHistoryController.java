/*
 * PatientHistoryController.java                                  19 mai. 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.patient.history;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/patients/history")
@Tag(name = "Patients history", description = "Patient management API")
public class PatientHistoryController {

    private final PatientHistoryService patientHistoryService;

    public PatientHistoryController(final PatientHistoryService patientHistoryService) {
        this.patientHistoryService = patientHistoryService;
    }

    @Operation(summary = "Get history", description = "Retrieves all history records filtered with params")
    @ApiResponse(responseCode = "200", description = "Histories")
    @GetMapping("/search")
    public List<PatientHistory> searchHistory(
            @RequestParam String keyword,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {

        return patientHistoryService.searchByMultipleCriteria(keyword, startDate, endDate);
    }
    
    @Operation(summary = "Get a patient history", description = "Retrieves a patient history")
    @ApiResponse(responseCode = "200", description = "Patient histories summaries")
    @GetMapping("/{patientId}/summary")
    public PatientSummaryResponse getPatientSummary(@PathVariable Long patientId) {
        return patientHistoryService.getPatientSummary(patientId);
    }
} 