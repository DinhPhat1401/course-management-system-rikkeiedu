package edu.rk.camel.coursemanagementsystem.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonDto {
    private Integer lessonId;
    private Integer courseId;
    private String title;
    private String textContent;
    private String contentUrl;
    private Integer orderIndex;
    private Boolean isPublished;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
