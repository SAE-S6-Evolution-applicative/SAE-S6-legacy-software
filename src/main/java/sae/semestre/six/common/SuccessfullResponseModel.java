/*
 * SuccessfullResponseModel.java                                  14 mai 2025
 * IUT de Rodez, pas de droit d'auteur
 */

package sae.semestre.six.common;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response model for successful operations")
public record SuccessfullResponseModel(
        String message,
        boolean success
) {
}
