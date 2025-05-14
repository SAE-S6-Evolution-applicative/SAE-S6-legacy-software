package sae.semestre.six.appointment.medicalact;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class MedicalActService {

    private MedicalActRepository medicalActRepository;

    private static Logger logger = LoggerFactory.getLogger(MedicalActService.class);


    public MedicalActService(MedicalActRepository medicalActRepository) {
        this.medicalActRepository = medicalActRepository;
    }

    /**
     * Finds medical acts by their IDs.
     *
     * If some ids are not found, an IllegalArgumentException is thrown.
     *
     * @param medicalActIds the IDs of the medical acts to find
     * @return a list of medical acts
     * @throws IllegalArgumentException if some IDs are not found
     */
    public List<MedicalAct> findByIds(Long[] medicalActIds) {
        if (medicalActIds == null || medicalActIds.length == 0) {
            return Collections.emptyList();
        }

        List<MedicalAct> medicalActs = medicalActRepository.findAllById(List.of(medicalActIds));

        if (medicalActs.size() != medicalActIds.length) {
            logger.warn("Medical Acts not found");
            var medicalActIdsFound = medicalActs.stream().map(MedicalAct::getId).toList();
            List<Long> missingIds = Arrays.stream(medicalActIds)
                    .filter(id -> !medicalActIdsFound.contains(id))
                    .toList();
            throw new IllegalArgumentException("Some medical act are not found: " + missingIds);
        }

        return medicalActs;
    }
}
