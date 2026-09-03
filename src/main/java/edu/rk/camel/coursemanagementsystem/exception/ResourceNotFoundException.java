package edu.rk.camel.coursemanagementsystem.exception;

public class ResourceNotFoundException extends ApiException {
    public ResourceNotFoundException(String message) {
        super("RESOURCE_NOT_FOUND", message, 404);
    }
}
