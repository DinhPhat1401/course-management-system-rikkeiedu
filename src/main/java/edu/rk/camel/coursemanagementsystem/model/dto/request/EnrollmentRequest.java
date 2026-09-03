package edu.rk.camel.coursemanagementsystem.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnrollmentRequest {
    @NotNull(message = "ID khóa học không được để trống")
    private Integer courseId;
}
