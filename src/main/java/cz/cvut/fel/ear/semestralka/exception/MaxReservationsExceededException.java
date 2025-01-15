package cz.cvut.fel.ear.semestralka.exception;

public class MaxReservationsExceededException extends RuntimeException {
    public MaxReservationsExceededException(String message) {
        super(message);
    }
}
