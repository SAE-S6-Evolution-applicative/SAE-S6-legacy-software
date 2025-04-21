/*
 * IntegrationTestUtils.java                                  21 avr. 2025
 * IUT de Rodez, pas de droit d'auteur
 */

package sae.semestre.six;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 */
public class IntegrationTestUtils {
    /**
     * Convert an Object to a JSON string.
     *
     * @param object the object to convert
     * @return the JSON string
     */
    public static String asJsonString(Object object) {
        try {
            String valueAsString = new ObjectMapper().writeValueAsString(object);
            System.out.println(valueAsString);
            return valueAsString;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
