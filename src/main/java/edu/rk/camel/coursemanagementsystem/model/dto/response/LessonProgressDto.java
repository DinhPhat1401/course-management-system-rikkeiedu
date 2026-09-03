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
public class LessonProgressDto {
    private Integer progressId;
    private Integer enrollmentId;
    private Integer lessonId;
    private Boolean isCompleted;
    private LocalDateTime completedAt;
    private LocalDateTime lastAccessedAt;
}
