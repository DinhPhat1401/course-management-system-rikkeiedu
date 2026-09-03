package edu.rk.camel.coursemanagementsystem.controller;

import edu.rk.camel.coursemanagementsystem.model.dto.request.CourseCreateRequest;
import edu.rk.camel.coursemanagementsystem.model.dto.request.CourseUpdateRequest;
import edu.rk.camel.coursemanagementsystem.model.dto.response.ApiResponse;
import edu.rk.camel.coursemanagementsystem.model.dto.response.CourseDto;
import edu.rk.camel.coursemanagementsystem.model.entity.CourseStatus;
import edu.rk.camel.coursemanagementsystem.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseDto>>> getCourses(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer teacher_id,
            @RequestParam(required = false) String status) {
        
        List<CourseDto> courses;

        if (search != null && !search.isEmpty()) {
            courses = courseService.searchCourses(search);
        } else if (teacher_id != null) {
            courses = courseService.getCoursesByTeacher(teacher_id);
        } else if (status != null) {
            courses = courseService.getCoursesByStatus(CourseStatus.valueOf(status.toUpperCase()));
        } else {
            // Default: Only return PUBLISHED courses for normal GET
            courses = courseService.getCoursesByStatus(CourseStatus.PUBLISHED);
        }

        return ResponseEntity.ok(ApiResponse.success(courses, "Lấy danh sách khóa học thành công", 200));
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<ApiResponse<CourseDto>> getCourseById(@PathVariable Integer courseId) {
        CourseDto course = courseService.getCourseById(courseId, true);
        return ResponseEntity.ok(ApiResponse.success(course, "Lấy chi tiết khóa học thành công", 200));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CourseDto>> createCourse(@Valid @RequestBody CourseCreateRequest request) {
        CourseDto course = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(course, "Tạo khóa học thành công", 201));
    }

    @PutMapping("/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CourseDto>> updateCourse(
            @PathVariable Integer courseId, 
            @Valid @RequestBody CourseUpdateRequest request) {
        CourseDto course = courseService.updateCourse(courseId, request);
        return ResponseEntity.ok(ApiResponse.success(course, "Cập nhật khóa học thành công", 200));
    }

    @PutMapping("/{courseId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CourseDto>> updateCourseStatus(
            @PathVariable Integer courseId, 
            @RequestBody Map<String, String> payload) {
        CourseStatus status = CourseStatus.valueOf(payload.get("status").toUpperCase());
        CourseDto course = courseService.updateCourseStatus(courseId, status);
        return ResponseEntity.ok(ApiResponse.success(course, "Cập nhật trạng thái thành công", 200));
    }

    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable Integer courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa khóa học thành công", 200));
    }
}
