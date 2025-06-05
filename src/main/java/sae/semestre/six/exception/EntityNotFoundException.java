/**
 * NoEntityFoundException.java                                  20 mai 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.exception;

/**
 * Exception thrown when an entity is not found in the database.
 * Used to replace common IllegalArgumentException when retrieving entities by ID.
 */
public class EntityNotFoundException extends RuntimeException {

    /**
     * Constructs a new NoEntityFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public EntityNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new NoEntityFoundException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}