package edu.rk.camel.coursemanagementsystem.controller;

import edu.rk.camel.coursemanagementsystem.model.dto.request.LessonCreateRequest;
import edu.rk.camel.coursemanagementsystem.model.dto.request.LessonUpdateRequest;
import edu.rk.camel.coursemanagementsystem.model.dto.response.ApiResponse;
import edu.rk.camel.coursemanagementsystem.model.dto.response.LessonDto;
import edu.rk.camel.coursemanagementsystem.service.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @GetMapping("/courses/{courseId}/lessons")
    public ResponseEntity<ApiResponse<List<LessonDto>>> getLessonsByCourse(@PathVariable Integer courseId) {
        // Chỉ lấy các bài học đã PUBLISHED
        List<LessonDto> lessons = lessonService.getLessonsByCourse(courseId, true);
        return ResponseEntity.ok(ApiResponse.success(lessons, "Lấy danh sách bài học thành công", 200));
    }

    @GetMapping("/lessons/{lessonId}")
    public ResponseEntity<ApiResponse<LessonDto>> getLessonById(@PathVariable Integer lessonId) {
        LessonDto lesson = lessonService.getLessonById(lessonId);
        return ResponseEntity.ok(ApiResponse.success(lesson, "Lấy chi tiết bài học thành công", 200));
    }

    @PostMapping("/courses/{courseId}/lessons")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<LessonDto>> createLesson(
            @PathVariable Integer courseId,
            @Valid @RequestBody LessonCreateRequest request) {
        LessonDto lesson = lessonService.createLesson(courseId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(lesson, "Thêm bài học thành công", 201));
    }

    @PutMapping("/lessons/{lessonId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<LessonDto>> updateLesson(
            @PathVariable Integer lessonId,
            @Valid @RequestBody LessonUpdateRequest request) {
        LessonDto lesson = lessonService.updateLesson(lessonId, request);
        return ResponseEntity.ok(ApiResponse.success(lesson, "Cập nhật bài học thành công", 200));
    }

    @PutMapping("/lessons/{lessonId}/publish")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<LessonDto>> updateLessonStatus(
            @PathVariable Integer lessonId,
            @RequestBody Map<String, Boolean> payload) {
        Boolean isPublished = payload.get("is_published");
        LessonDto lesson = lessonService.updateLessonStatus(lessonId, isPublished);
        return ResponseEntity.ok(ApiResponse.success(lesson, "Cập nhật trạng thái hiển thị thành công", 200));
    }

    @DeleteMapping("/lessons/{lessonId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteLesson(@PathVariable Integer lessonId) {
        lessonService.deleteLesson(lessonId);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa bài học thành công", 200));
    }

    @GetMapping("/lessons/{lessonId}/content_preview")
    public ResponseEntity<ApiResponse<String>> getLessonContentPreview(@PathVariable Integer lessonId) {
        String preview = lessonService.getLessonContentPreview(lessonId);
        return ResponseEntity.ok(ApiResponse.success(preview, "Lấy nội dung xem trước thành công", 200));
    }
}
