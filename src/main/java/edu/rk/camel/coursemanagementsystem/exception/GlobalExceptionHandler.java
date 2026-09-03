package edu.rk.camel.coursemanagementsystem.exception;

import edu.rk.camel.coursemanagementsystem.model.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException ex) {
        log.warn("API Exception: {}", ex.getMessage());
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(ApiResponse.error(ex.getErrorCode(), ex.getMessage(), ex.getStatusCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        List<ApiResponse.FieldError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ApiResponse.FieldError(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.validationError(
                        "INVALID_INPUT_DATA",
                        "Dữ liệu đầu vào không hợp lệ",
                        HttpStatus.BAD_REQUEST.value(),
                        errors
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalException(Exception ex) {
        log.error("Internal Server Error: ", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        "INTERNAL_SERVER_ERROR",
                        "Đã xảy ra lỗi hệ thống, vui lòng thử lại sau.",
                        HttpStatus.INTERNAL_SERVER_ERROR.value()
                ));
    }
}
