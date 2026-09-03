package edu.rk.camel.coursemanagementsystem.model.dto.response;

import edu.rk.camel.coursemanagementsystem.model.entity.CourseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto {
    private Integer courseId;
    private String title;
    private String description;
    private UserDto teacher;
    private BigDecimal price;
    private Integer durationHours;
    private CourseStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
