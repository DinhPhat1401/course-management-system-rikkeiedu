package edu.rk.camel.coursemanagementsystem.service;

import edu.rk.camel.coursemanagementsystem.model.dto.request.EnrollmentRequest;
import edu.rk.camel.coursemanagementsystem.model.dto.response.EnrollmentDto;
import edu.rk.camel.coursemanagementsystem.model.dto.response.LessonProgressDto;

import java.util.List;

public interface EnrollmentService {
    EnrollmentDto enrollCourse(EnrollmentRequest request);
    List<EnrollmentDto> getStudentEnrollments();
    EnrollmentDto getEnrollmentDetails(Integer enrollmentId);
    LessonProgressDto completeLesson(Integer enrollmentId, Integer lessonId);
}
