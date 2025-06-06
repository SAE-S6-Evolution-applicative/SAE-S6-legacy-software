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
import sae.semestre.six.appointment.Appointment;
import sae.semestre.six.appointment.bill.Bill;
import sae.semestre.six.appointment.prescription.Prescription;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/patients/history")
@Tag(name = "Patients history", description = "Patient management API")
public class PatientHistoryController {

    private final PatientHistoryService patientHistoryService;

    @Autowired
    public PatientHistoryController(final PatientHistoryService patientHistoryService) {
        this.patientHistoryService = patientHistoryService;
    }

    @Operation(summary = "Get history", description = "Retrieves all history records filtered with params")
    @ApiResponse(responseCode = "200", description = "Histories")
    @GetMapping("/search")
    public List<PatientHistoryResponse> searchHistory(
            @RequestParam String keyword,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {

        return patientHistoryService.searchByMultipleCriteria(keyword, startDate, endDate).stream()
                .map(PatientHistoryResponse::new)
                .toList();
    }

    @Operation(summary = "Get a patient history", description = "Retrieves a patient history")
    @ApiResponse(responseCode = "200", description = "Patient histories summaries")
    @GetMapping("/{patientId}/summary")
    public PatientSummaryResponse getPatientSummary(@PathVariable Long patientId) {
        return patientHistoryService.getPatientSummary(patientId);
    }

    public record PatientHistoryResponse(
            Long patientId,
            String patientName,
            List<Long> appointmentIds,
            List<Long> prescriptionIds,
            List<Long> treatmentsIds,
            List<String> billsNumber,
            List<Long> labResultIds
    ) {
        public PatientHistoryResponse(PatientHistory patientHistory) {
            this(
                    patientHistory.getPatient().getId(),
                    patientHistory.getPatient().getFirstName() + " " + patientHistory.getPatient().getLastName(),
                    patientHistory.getAppointments().stream().map(Appointment::getId).toList(),
                    patientHistory.getPrescriptions().stream().map(Prescription::getId).toList(),
                    patientHistory.getTreatments().stream().map(Treatment::getId).toList(),
                    patientHistory.getBills().stream().map(Bill::getBillNumber).toList(),
                    patientHistory.getLabResults().stream().map(LabResult::getId).toList()
            );
        }
    }
} 