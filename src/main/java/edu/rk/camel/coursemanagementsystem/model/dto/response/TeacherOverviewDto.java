package edu.rk.camel.coursemanagementsystem.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherOverviewDto {
    private Integer teacherId;
    private Long totalCourses;
    private Long totalStudents;
    private Double averageRating;
}
