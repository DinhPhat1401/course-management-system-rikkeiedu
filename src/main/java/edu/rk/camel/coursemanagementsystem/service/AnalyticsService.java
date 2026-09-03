package edu.rk.camel.coursemanagementsystem.service;

import edu.rk.camel.coursemanagementsystem.model.dto.response.StudentProgressDto;
import edu.rk.camel.coursemanagementsystem.model.dto.response.TeacherOverviewDto;
import edu.rk.camel.coursemanagementsystem.model.dto.response.TopCourseDto;

import java.util.List;

public interface AnalyticsService {
    List<TopCourseDto> getTopCourses(int limit);
    StudentProgressDto getStudentProgress(Integer studentId);
    TeacherOverviewDto getTeacherOverview(Integer teacherId);
}
