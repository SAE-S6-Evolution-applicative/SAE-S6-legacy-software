/*
 * GlobalExceptionHandler.java                                  19 mai. 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public static final String DESCRIPTION = "description";
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail handleGenericException(Exception exception) {
        LOG.error("Internal Server Error: {}", exception.getMessage());
        LOG.error("Stack trace: ", exception);
        return createProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(), "Unknown internal server error.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ProblemDetail handleUnauthorizeException(IllegalArgumentException exception) {
        LOG.error("Illegal Argument Exception: {}", exception.getMessage());
        LOG.error("Stack trace: ", exception);
        return createProblemDetail(HttpStatus.UNAUTHORIZED, exception.getMessage(), "Bad request, all arguments must be provided.");
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleEntityNotFoundException(EntityNotFoundException exception) {
        LOG.error("Entity not found: {}", exception.getMessage());
        return createProblemDetail(HttpStatus.NOT_FOUND, exception.getMessage(),
                "The requested entity does not exist in the database.");
    }



    /**
     * Create a ProblemDetail object with the status, message and description.
     *
     * @param status      the HTTP status
     * @param message     the message
     * @param description the description
     * @return the ProblemDetail object
     */
    private ProblemDetail createProblemDetail(HttpStatus status, String message, String description) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(status, message);
        errorDetail.setProperty(DESCRIPTION, description);
        return errorDetail;
    }
}