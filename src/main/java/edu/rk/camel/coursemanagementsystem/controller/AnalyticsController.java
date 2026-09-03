package edu.rk.camel.coursemanagementsystem.controller;

import edu.rk.camel.coursemanagementsystem.model.dto.response.ApiResponse;
import edu.rk.camel.coursemanagementsystem.model.dto.response.StudentProgressDto;
import edu.rk.camel.coursemanagementsystem.model.dto.response.TeacherOverviewDto;
import edu.rk.camel.coursemanagementsystem.model.dto.response.TopCourseDto;
import edu.rk.camel.coursemanagementsystem.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/top-courses")
    public ResponseEntity<ApiResponse<List<TopCourseDto>>> getTopCourses(@RequestParam(defaultValue = "10") int limit) {
        List<TopCourseDto> topCourses = analyticsService.getTopCourses(limit);
        return ResponseEntity.ok(ApiResponse.success(topCourses, "Lấy danh sách khóa học phổ biến thành công", 200));
    }

    @GetMapping("/student-progress/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER') or (hasRole('STUDENT') and principal.user.userId == #studentId)")
    public ResponseEntity<ApiResponse<StudentProgressDto>> getStudentProgress(@PathVariable Integer studentId) {
        StudentProgressDto progress = analyticsService.getStudentProgress(studentId);
        return ResponseEntity.ok(ApiResponse.success(progress, "Lấy thống kê tiến độ học viên thành công", 200));
    }

    @GetMapping("/teacher-overview/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN') or (hasRole('TEACHER') and principal.user.userId == #teacherId)")
    public ResponseEntity<ApiResponse<TeacherOverviewDto>> getTeacherOverview(@PathVariable Integer teacherId) {
        TeacherOverviewDto overview = analyticsService.getTeacherOverview(teacherId);
        return ResponseEntity.ok(ApiResponse.success(overview, "Lấy thống kê tổng quan giảng viên thành công", 200));
    }
}
