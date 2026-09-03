package edu.rk.camel.coursemanagementsystem.controller;

import edu.rk.camel.coursemanagementsystem.model.dto.request.EnrollmentRequest;
import edu.rk.camel.coursemanagementsystem.model.dto.response.ApiResponse;
import edu.rk.camel.coursemanagementsystem.model.dto.response.EnrollmentDto;
import edu.rk.camel.coursemanagementsystem.model.dto.response.LessonProgressDto;
import edu.rk.camel.coursemanagementsystem.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<EnrollmentDto>>> getEnrollments() {
        List<EnrollmentDto> enrollments = enrollmentService.getStudentEnrollments();
        return ResponseEntity.ok(ApiResponse.success(enrollments, "Lấy danh sách khóa học đã đăng ký thành công", 200));
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<EnrollmentDto>> enrollCourse(@Valid @RequestBody EnrollmentRequest request) {
        EnrollmentDto enrollment = enrollmentService.enrollCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(enrollment, "Đăng ký khóa học thành công", 201));
    }

    @GetMapping("/{enrollmentId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<EnrollmentDto>> getEnrollmentDetails(@PathVariable Integer enrollmentId) {
        EnrollmentDto enrollment = enrollmentService.getEnrollmentDetails(enrollmentId);
        return ResponseEntity.ok(ApiResponse.success(enrollment, "Lấy thông tin đăng ký thành công", 200));
    }

    @PutMapping("/{enrollmentId}/complete_lesson/{lessonId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<LessonProgressDto>> completeLesson(
            @PathVariable Integer enrollmentId,
            @PathVariable Integer lessonId) {
        LessonProgressDto progress = enrollmentService.completeLesson(enrollmentId, lessonId);
        return ResponseEntity.ok(ApiResponse.success(progress, "Đánh dấu bài học hoàn thành thành công", 200));
    }
}
