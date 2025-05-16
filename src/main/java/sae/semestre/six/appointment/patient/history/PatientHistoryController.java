package sae.semestre.six.appointment.patient.history;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/patient-history")
public class PatientHistoryController {
    
    @Autowired
    private PatientHistoryRepository patientHistoryRepository;
    
    
    @GetMapping("/search")
    public List<PatientHistory> searchHistory(
            @RequestParam String keyword,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        
        
//        List<PatientHistory> results = patientHistoryDao.searchByMultipleCriteria(
//            keyword, startDate, endDate);
//
//        return results;
        return  Collections.emptyList();
    }
    
    
    @GetMapping("/patient/{patientId}/summary")
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