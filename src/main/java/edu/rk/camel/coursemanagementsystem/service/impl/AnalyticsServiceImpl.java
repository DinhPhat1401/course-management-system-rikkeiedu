package edu.rk.camel.coursemanagementsystem.service.impl;

import edu.rk.camel.coursemanagementsystem.exception.ResourceNotFoundException;
import edu.rk.camel.coursemanagementsystem.model.dto.response.CourseDto;
import edu.rk.camel.coursemanagementsystem.model.dto.response.StudentProgressDto;
import edu.rk.camel.coursemanagementsystem.model.dto.response.TeacherOverviewDto;
import edu.rk.camel.coursemanagementsystem.model.dto.response.TopCourseDto;
import edu.rk.camel.coursemanagementsystem.model.dto.response.UserDto;
import edu.rk.camel.coursemanagementsystem.model.entity.Course;
import edu.rk.camel.coursemanagementsystem.model.entity.EnrollmentStatus;
import edu.rk.camel.coursemanagementsystem.model.entity.User;
import edu.rk.camel.coursemanagementsystem.repository.CourseRepository;
import edu.rk.camel.coursemanagementsystem.repository.EnrollmentRepository;
import edu.rk.camel.coursemanagementsystem.repository.ReviewRepository;
import edu.rk.camel.coursemanagementsystem.repository.UserRepository;
import edu.rk.camel.coursemanagementsystem.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @Override
    public List<TopCourseDto> getTopCourses(int limit) {
        List<Object[]> results = courseRepository.findTopCoursesByEnrollments(PageRequest.of(0, limit));
        List<TopCourseDto> topCourses = new ArrayList<>();
        
        for (Object[] result : results) {
            Course course = (Course) result[0];
            Long count = (Long) result[1];
            
            topCourses.add(TopCourseDto.builder()
                    .course(mapCourseToDto(course))
                    .enrollmentCount(count)
                    .build());
        }
        
        return topCourses;
    }

    @Override
    public StudentProgressDto getStudentProgress(Integer studentId) {
        userRepository.findById(studentId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sinh viên"));
        
        List<Object[]> statusCounts = enrollmentRepository.countStudentProgressByStatus(studentId);
        
        long totalEnrolled = 0;
        long totalCompleted = 0;
        long totalInProgress = 0;
        
        for (Object[] statusCount : statusCounts) {
            EnrollmentStatus status = (EnrollmentStatus) statusCount[0];
            Long count = (Long) statusCount[1];
            
            totalEnrolled += count;
            if (status == EnrollmentStatus.COMPLETED) {
                totalCompleted += count;
            } else if (status == EnrollmentStatus.ENROLLED) {
                totalInProgress += count;
            }
        }
        
        return StudentProgressDto.builder()
                .studentId(studentId)
                .totalEnrolled(totalEnrolled)
                .totalCompleted(totalCompleted)
                .totalInProgress(totalInProgress)
                .build();
    }

    @Override
    public TeacherOverviewDto getTeacherOverview(Integer teacherId) {
        userRepository.findById(teacherId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên"));
        
        long totalCourses = courseRepository.countByTeacher_UserId(teacherId);
        long totalStudents = enrollmentRepository.countTotalStudentsForTeacher(teacherId);
        Double averageRating = reviewRepository.getAverageRatingForTeacher(teacherId);
        
        return TeacherOverviewDto.builder()
                .teacherId(teacherId)
                .totalCourses(totalCourses)
                .totalStudents(totalStudents)
                .averageRating(averageRating != null ? averageRating : 0.0)
                .build();
    }

    private CourseDto mapCourseToDto(Course course) {
        User teacher = course.getTeacher();
        UserDto teacherDto = UserDto.builder()
                .userId(teacher.getUserId())
                .username(teacher.getUsername())
                .fullName(teacher.getFullName())
                .build();

        return CourseDto.builder()
                .courseId(course.getCourseId())
                .title(course.getTitle())
                .description(course.getDescription())
                .teacher(teacherDto)
                .price(course.getPrice())
                .durationHours(course.getDurationHours())
                .status(course.getStatus())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }
}
