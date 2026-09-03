package edu.rk.camel.coursemanagementsystem.model.dto.response;

import edu.rk.camel.coursemanagementsystem.model.entity.EnrollmentStatus;
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
public class EnrollmentDto {
    private Integer enrollmentId;
    private Integer studentId;
    private CourseDto course;
    private LocalDateTime enrollmentDate;
    private EnrollmentStatus status;
    private LocalDateTime completionDate;
    private BigDecimal progressPercentage;
}
