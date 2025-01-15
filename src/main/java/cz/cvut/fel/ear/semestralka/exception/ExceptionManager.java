package cz.cvut.fel.ear.semestralka.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionManager extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleCustomExceptions(RuntimeException e) {
        if (e instanceof UserNotFoundException || e instanceof AdminNotFoundException || e instanceof MembershipNotFoundException) {
            return buildResponseEntity(HttpStatus.NOT_FOUND, e.getMessage());
        } else if (e instanceof FacilityException) {
            return buildResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage());
        } else if (e instanceof InvalidReservationException) {
            return buildResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        } else if (e instanceof MaxReservationsExceededException) {
            return buildResponseEntity(HttpStatus.CONFLICT, e.getMessage());
        }
        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    private ResponseEntity<Object> buildResponseEntity(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("time", LocalDateTime.now());
        return new ResponseEntity<>(body, status);
    }
}
