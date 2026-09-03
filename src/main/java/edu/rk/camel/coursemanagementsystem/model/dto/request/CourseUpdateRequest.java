package edu.rk.camel.coursemanagementsystem.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CourseUpdateRequest {
    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    private String description;

    @Min(value = 0, message = "Giá khóa học không được âm")
    private BigDecimal price;

    @Min(value = 0, message = "Thời lượng khóa học không hợp lệ")
    private Integer durationHours;
}
