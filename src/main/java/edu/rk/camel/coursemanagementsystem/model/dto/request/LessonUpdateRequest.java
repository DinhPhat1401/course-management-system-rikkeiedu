package edu.rk.camel.coursemanagementsystem.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LessonUpdateRequest {
    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    private String textContent;
    private String contentUrl;
    private Integer orderIndex;
}
