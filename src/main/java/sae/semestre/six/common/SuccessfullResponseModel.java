/*
 * SuccessfullResponseModel.java                                  19 mai. 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.common;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response model for successful operations")
public record SuccessfullResponseModel(
        String message,
        boolean success
) {
}
