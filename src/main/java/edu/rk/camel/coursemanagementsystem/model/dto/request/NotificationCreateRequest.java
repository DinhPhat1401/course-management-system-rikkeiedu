package edu.rk.camel.coursemanagementsystem.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificationCreateRequest {
    @NotNull(message = "ID người dùng không được để trống")
    private Integer userId;

    @NotBlank(message = "Nội dung thông báo không được để trống")
    private String message;

    private String type;
    private String targetUrl;
}
