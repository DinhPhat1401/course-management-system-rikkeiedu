package edu.rk.camel.coursemanagementsystem.controller;

import edu.rk.camel.coursemanagementsystem.model.dto.request.ReviewRequest;
import edu.rk.camel.coursemanagementsystem.model.dto.response.ApiResponse;
import edu.rk.camel.coursemanagementsystem.model.dto.response.ReviewDto;
import edu.rk.camel.coursemanagementsystem.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/courses/{courseId}/reviews")
    public ResponseEntity<ApiResponse<List<ReviewDto>>> getCourseReviews(@PathVariable Integer courseId) {
        List<ReviewDto> reviews = reviewService.getCourseReviews(courseId);
        return ResponseEntity.ok(ApiResponse.success(reviews, "Lấy danh sách đánh giá thành công", 200));
    }

    @PostMapping("/courses/{courseId}/reviews")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<ReviewDto>> submitReview(
            @PathVariable Integer courseId,
            @Valid @RequestBody ReviewRequest request) {
        ReviewDto review = reviewService.submitReview(courseId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(review, "Gửi đánh giá thành công", 201));
    }

    @PutMapping("/reviews/{reviewId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<ApiResponse<ReviewDto>> updateReview(
            @PathVariable Integer reviewId,
            @Valid @RequestBody ReviewRequest request) {
        ReviewDto review = reviewService.updateReview(reviewId, request);
        return ResponseEntity.ok(ApiResponse.success(review, "Cập nhật đánh giá thành công", 200));
    }

    @DeleteMapping("/reviews/{reviewId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Integer reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa đánh giá thành công", 200));
    }
}
