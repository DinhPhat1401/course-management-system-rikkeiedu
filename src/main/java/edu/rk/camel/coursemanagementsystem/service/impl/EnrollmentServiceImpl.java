package edu.rk.camel.coursemanagementsystem.service.impl;

import edu.rk.camel.coursemanagementsystem.exception.ApiException;
import edu.rk.camel.coursemanagementsystem.exception.ResourceNotFoundException;
import edu.rk.camel.coursemanagementsystem.model.dto.request.EnrollmentRequest;
import edu.rk.camel.coursemanagementsystem.model.dto.response.CourseDto;
import edu.rk.camel.coursemanagementsystem.model.dto.response.EnrollmentDto;
import edu.rk.camel.coursemanagementsystem.model.dto.response.LessonProgressDto;
import edu.rk.camel.coursemanagementsystem.model.dto.response.UserDto;
import edu.rk.camel.coursemanagementsystem.model.entity.*;
import edu.rk.camel.coursemanagementsystem.repository.CourseRepository;
import edu.rk.camel.coursemanagementsystem.repository.EnrollmentRepository;
import edu.rk.camel.coursemanagementsystem.repository.LessonProgressRepository;
import edu.rk.camel.coursemanagementsystem.repository.LessonRepository;
import edu.rk.camel.coursemanagementsystem.security.CustomUserDetails;
import edu.rk.camel.coursemanagementsystem.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final LessonProgressRepository lessonProgressRepository;

    @Override
    @Transactional
    public EnrollmentDto enrollCourse(EnrollmentRequest request) {
        User currentUser = getCurrentUser();

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học"));

        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new ApiException("COURSE_NOT_AVAILABLE", "Khóa học chưa được xuất bản", 400);
        }

        if (enrollmentRepository.findByStudent_UserIdAndCourse_CourseId(currentUser.getUserId(), course.getCourseId()).isPresent()) {
            throw new ApiException("DUPLICATE_RESOURCE", "Bạn đã đăng ký khóa học này rồi", 400);
        }

        Enrollment enrollment = Enrollment.builder()
                .student(currentUser)
                .course(course)
                .status(EnrollmentStatus.ENROLLED)
                .progressPercentage(BigDecimal.ZERO)
                .build();

        return mapToDto(enrollmentRepository.save(enrollment));
    }

    @Override
    public List<EnrollmentDto> getStudentEnrollments() {
        User currentUser = getCurrentUser();
        return enrollmentRepository.findByStudent_UserId(currentUser.getUserId())
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public EnrollmentDto getEnrollmentDetails(Integer enrollmentId) {
        Enrollment enrollment = getAndValidateEnrollment(enrollmentId);
        return mapToDto(enrollment);
    }

    @Override
    @Transactional
    public LessonProgressDto completeLesson(Integer enrollmentId, Integer lessonId) {
        Enrollment enrollment = getAndValidateEnrollment(enrollmentId);

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học"));

        if (!lesson.getCourse().getCourseId().equals(enrollment.getCourse().getCourseId())) {
            throw new ApiException("INVALID_INPUT_DATA", "Bài học không thuộc khóa học này", 400);
        }

        if (!lesson.getIsPublished()) {
            throw new ApiException("LESSON_NOT_AVAILABLE", "Bài học chưa được xuất bản", 400);
        }

        LessonProgress progress = lessonProgressRepository
                .findByEnrollment_EnrollmentIdAndLesson_LessonId(enrollmentId, lessonId)
                .orElseGet(() -> LessonProgress.builder()
                        .enrollment(enrollment)
                        .lesson(lesson)
                        .isCompleted(false)
                        .build());

        if (!progress.getIsCompleted()) {
            progress.setIsCompleted(true);
            progress.setCompletedAt(LocalDateTime.now());
            progress = lessonProgressRepository.save(progress);

            recalculateProgress(enrollment);
        }

        return LessonProgressDto.builder()
                .progressId(progress.getProgressId())
                .enrollmentId(enrollmentId)
                .lessonId(lessonId)
                .isCompleted(progress.getIsCompleted())
                .completedAt(progress.getCompletedAt())
                .lastAccessedAt(progress.getLastAccessedAt())
                .build();
    }

    private void recalculateProgress(Enrollment enrollment) {
        long totalPublishedLessons = lessonRepository.findByCourse_CourseIdAndIsPublished(enrollment.getCourse().getCourseId(), true).size();
        if (totalPublishedLessons == 0) {
            return;
        }

        long completedLessons = lessonProgressRepository.countByEnrollment_EnrollmentIdAndIsCompletedTrue(enrollment.getEnrollmentId());

        BigDecimal progress = BigDecimal.valueOf(completedLessons)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalPublishedLessons), 2, RoundingMode.HALF_UP);

        enrollment.setProgressPercentage(progress);

        if (progress.compareTo(BigDecimal.valueOf(100)) >= 0) {
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
            enrollment.setCompletionDate(LocalDateTime.now());
        } else {
            enrollment.setStatus(EnrollmentStatus.ENROLLED);
        }

        enrollmentRepository.save(enrollment);
    }

    private Enrollment getAndValidateEnrollment(Integer enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin đăng ký"));

        User currentUser = getCurrentUser();
        if (!enrollment.getStudent().getUserId().equals(currentUser.getUserId())) {
            throw new ApiException("ACCESS_DENIED", "Bạn không có quyền truy cập thông tin này", 403);
        }
        return enrollment;
    }

    private User getCurrentUser() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUser();
    }

    private EnrollmentDto mapToDto(Enrollment enrollment) {
        User teacher = enrollment.getCourse().getTeacher();
        UserDto teacherDto = UserDto.builder()
                .userId(teacher.getUserId())
                .username(teacher.getUsername())
                .email(teacher.getEmail())
                .fullName(teacher.getFullName())
                .build();

        CourseDto courseDto = CourseDto.builder()
                .courseId(enrollment.getCourse().getCourseId())
                .title(enrollment.getCourse().getTitle())
                .description(enrollment.getCourse().getDescription())
                .teacher(teacherDto)
                .price(enrollment.getCourse().getPrice())
                .durationHours(enrollment.getCourse().getDurationHours())
                .status(enrollment.getCourse().getStatus())
                .build();

        return EnrollmentDto.builder()
                .enrollmentId(enrollment.getEnrollmentId())
                .studentId(enrollment.getStudent().getUserId())
                .course(courseDto)
                .enrollmentDate(enrollment.getEnrollmentDate())
                .status(enrollment.getStatus())
                .completionDate(enrollment.getCompletionDate())
                .progressPercentage(enrollment.getProgressPercentage())
                .build();
    }
}
