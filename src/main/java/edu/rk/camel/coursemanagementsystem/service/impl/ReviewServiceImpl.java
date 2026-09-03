package edu.rk.camel.coursemanagementsystem.service.impl;

import edu.rk.camel.coursemanagementsystem.exception.ApiException;
import edu.rk.camel.coursemanagementsystem.exception.ResourceNotFoundException;
import edu.rk.camel.coursemanagementsystem.model.dto.request.ReviewRequest;
import edu.rk.camel.coursemanagementsystem.model.dto.response.ReviewDto;
import edu.rk.camel.coursemanagementsystem.model.dto.response.UserDto;
import edu.rk.camel.coursemanagementsystem.model.entity.*;
import edu.rk.camel.coursemanagementsystem.repository.CourseRepository;
import edu.rk.camel.coursemanagementsystem.repository.EnrollmentRepository;
import edu.rk.camel.coursemanagementsystem.repository.ReviewRepository;
import edu.rk.camel.coursemanagementsystem.security.CustomUserDetails;
import edu.rk.camel.coursemanagementsystem.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public List<ReviewDto> getCourseReviews(Integer courseId) {
        return reviewRepository.findByCourse_CourseId(courseId)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReviewDto submitReview(Integer courseId, ReviewRequest request) {
        User currentUser = getCurrentUser();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học"));

        Enrollment enrollment = enrollmentRepository.findByStudent_UserIdAndCourse_CourseId(currentUser.getUserId(), courseId)
                .orElseThrow(() -> new ApiException("ACCESS_DENIED", "Bạn phải đăng ký khóa học này mới được đánh giá", 403));

        if (reviewRepository.findByCourse_CourseIdAndStudent_UserId(courseId, currentUser.getUserId()).isPresent()) {
            throw new ApiException("DUPLICATE_RESOURCE", "Bạn đã đánh giá khóa học này rồi", 400);
        }

        Review review = Review.builder()
                .course(course)
                .student(currentUser)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        return mapToDto(reviewRepository.save(review));
    }

    @Override
    @Transactional
    public ReviewDto updateReview(Integer reviewId, ReviewRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá"));

        checkOwnerOrAdmin(review);

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        return mapToDto(reviewRepository.save(review));
    }

    @Override
    @Transactional
    public void deleteReview(Integer reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá"));

        checkOwnerOrAdmin(review);

        reviewRepository.delete(review);
    }

    private void checkOwnerOrAdmin(Review review) {
        User currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN && !review.getStudent().getUserId().equals(currentUser.getUserId())) {
            throw new ApiException("ACCESS_DENIED", "Bạn không có quyền thao tác", 403);
        }
    }

    private User getCurrentUser() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUser();
    }

    private ReviewDto mapToDto(Review review) {
        User student = review.getStudent();
        UserDto studentDto = UserDto.builder()
                .userId(student.getUserId())
                .username(student.getUsername())
                .fullName(student.getFullName())
                .build();

        return ReviewDto.builder()
                .reviewId(review.getReviewId())
                .courseId(review.getCourse().getCourseId())
                .student(studentDto)
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
