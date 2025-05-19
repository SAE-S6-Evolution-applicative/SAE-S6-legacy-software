/*
 * MedicalActRequest.java 15 may 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.appointment.medicalact;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

@Schema(description = "Request model for creating a new medical action")
public record MedicalActRequest(
        @Schema(description = "Name of the medical action")
        String name,

        @Min(value = 0, message = "Price must be positive")
        @Schema(description = "Price of the medical action", minimum = "0")
        double price
) {
}

