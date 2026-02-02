package it.ticketflow.ticketflow.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String resourceName, Object id) {
        super(resourceName + " con id " + id + " non trovato");
    }
}