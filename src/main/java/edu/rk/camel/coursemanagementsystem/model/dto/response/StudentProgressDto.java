package edu.rk.camel.coursemanagementsystem.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProgressDto {
    private Integer studentId;
    private Long totalEnrolled;
    private Long totalCompleted;
    private Long totalInProgress;
}
