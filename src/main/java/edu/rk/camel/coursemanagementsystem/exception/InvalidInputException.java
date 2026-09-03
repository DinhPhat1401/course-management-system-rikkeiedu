package edu.rk.camel.coursemanagementsystem.exception;

public class InvalidInputException extends ApiException {
    public InvalidInputException(String message) {
        super("INVALID_INPUT_DATA", message, 400);
    }
}
