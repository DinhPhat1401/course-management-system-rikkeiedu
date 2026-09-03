package edu.rk.camel.coursemanagementsystem.service;

import edu.rk.camel.coursemanagementsystem.model.dto.request.CourseCreateRequest;
import edu.rk.camel.coursemanagementsystem.model.dto.request.CourseUpdateRequest;
import edu.rk.camel.coursemanagementsystem.model.dto.response.CourseDto;
import edu.rk.camel.coursemanagementsystem.model.entity.CourseStatus;

import java.util.List;

public interface CourseService {
    List<CourseDto> getCoursesByStatus(CourseStatus status);
    List<CourseDto> searchCourses(String keyword);
    List<CourseDto> getCoursesByTeacher(Integer teacherId);
    CourseDto getCourseById(Integer courseId, boolean isAuth);
    CourseDto createCourse(CourseCreateRequest request);
    CourseDto updateCourse(Integer courseId, CourseUpdateRequest request);
    CourseDto updateCourseStatus(Integer courseId, CourseStatus status);
    void deleteCourse(Integer courseId);
}
