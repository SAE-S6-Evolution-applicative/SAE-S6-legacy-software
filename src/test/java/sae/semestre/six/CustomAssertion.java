/*
 * CustomAssertion.java                                  28 apr. 2025
 * IUT de Rodez, no author rights
 */
package sae.semestre.six;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * @author François de Saint Palais
 */
public class CustomAssertion {

    public static void assertListEquals(List<?> expected, List<?> actual) {
        assertEquals(expected.size(), actual.size(), "Size should be the same");
        if (!expected.isEmpty() && !actual.isEmpty()) {
            Class<?> expectedClass = expected.getFirst().getClass();
            assertInstanceOf(expectedClass, actual.getFirst().getClass());
        }
        for (int i = 0; i < expected.size(); i++) {
            Object expectedElement = expected.get(i);
            Object actualElement = actual.get(i);
            assertEquals(expectedElement, actualElement, "Elements at index " + i + " should be the same");
        }
    }

    public static void assertListNotEquals(List<?> expected, List<?> actual, String message) {
        boolean equals = true;
        if (expected.size() == actual.size()) {
            for (int i = 0; i < expected.size() && equals; i++) {
                Object expectedElement = expected.get(i);
                Object actualElement = actual.get(i);
                equals = expectedElement.equals(actualElement);
            }
        } else {
            equals = false;
        }

        if (equals) {
            String errorMessage = message;
            if (errorMessage == null || errorMessage.isBlank()) {
                errorMessage = "Lists should not be equal";
            }
            throw new AssertionError(errorMessage);
        }
    }
}
