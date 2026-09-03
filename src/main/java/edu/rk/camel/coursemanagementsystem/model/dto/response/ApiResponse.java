package edu.rk.camel.coursemanagementsystem.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    private boolean success;
    
    @JsonProperty("status_code")
    private int statusCode;
    
    private String message;
    
    private T data;
    
    @JsonProperty("error_code")
    private String errorCode;
    
    private List<FieldError> errors;
    
    @Builder.Default
    private String timestamp = Instant.now().toString();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FieldError {
        private String field;
        private String message;
    }
    
    // Helper method for success
    public static <T> ApiResponse<T> success(T data, String message, int statusCode) {
        return ApiResponse.<T>builder()
                .success(true)
                .statusCode(statusCode)
                .message(message)
                .data(data)
                .build();
    }

    // Helper method for generic error
    public static <T> ApiResponse<T> error(String errorCode, String message, int statusCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .statusCode(statusCode)
                .errorCode(errorCode)
                .message(message)
                .build();
    }
    
    // Helper method for validation error
    public static <T> ApiResponse<T> validationError(String errorCode, String message, int statusCode, List<FieldError> errors) {
        return ApiResponse.<T>builder()
                .success(false)
                .statusCode(statusCode)
                .errorCode(errorCode)
                .message(message)
                .errors(errors)
                .build();
    }
}
