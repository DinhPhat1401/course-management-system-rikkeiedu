package edu.rk.camel.coursemanagementsystem.service.impl;

import edu.rk.camel.coursemanagementsystem.exception.ApiException;
import edu.rk.camel.coursemanagementsystem.exception.ResourceNotFoundException;
import edu.rk.camel.coursemanagementsystem.model.dto.request.CourseCreateRequest;
import edu.rk.camel.coursemanagementsystem.model.dto.request.CourseUpdateRequest;
import edu.rk.camel.coursemanagementsystem.model.dto.response.CourseDto;
import edu.rk.camel.coursemanagementsystem.model.dto.response.UserDto;
import edu.rk.camel.coursemanagementsystem.model.entity.Course;
import edu.rk.camel.coursemanagementsystem.model.entity.CourseStatus;
import edu.rk.camel.coursemanagementsystem.model.entity.Role;
import edu.rk.camel.coursemanagementsystem.model.entity.User;
import edu.rk.camel.coursemanagementsystem.repository.CourseRepository;
import edu.rk.camel.coursemanagementsystem.repository.UserRepository;
import edu.rk.camel.coursemanagementsystem.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Override
    public List<CourseDto> getCoursesByStatus(CourseStatus status) {
        if (status == null) {
            return courseRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
        }
        return courseRepository.findByStatus(status).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<CourseDto> searchCourses(String keyword) {
        return courseRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<CourseDto> getCoursesByTeacher(Integer teacherId) {
        return courseRepository.findByTeacher_UserId(teacherId)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public CourseDto getCourseById(Integer courseId, boolean isAuth) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học"));
        
        // If it's a student (Auth), maybe they can only see PUBLISHED courses unless enrolled, but SRS says AUTH can see course details.
        // But for simplicity, return course details.
        return mapToDto(course);
    }

    @Override
    @Transactional
    public CourseDto createCourse(CourseCreateRequest request) {
        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên"));

        if (teacher.getRole() != Role.TEACHER) {
            throw new ApiException("INVALID_INPUT_DATA", "Người dùng không phải là giảng viên", 400);
        }

        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .teacher(teacher)
                .price(request.getPrice())
                .durationHours(request.getDurationHours())
                .status(CourseStatus.DRAFT)
                .build();

        return mapToDto(courseRepository.save(course));
    }

    @Override
    @Transactional
    public CourseDto updateCourse(Integer courseId, CourseUpdateRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học"));

        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        if (request.getPrice() != null) course.setPrice(request.getPrice());
        if (request.getDurationHours() != null) course.setDurationHours(request.getDurationHours());

        return mapToDto(courseRepository.save(course));
    }

    @Override
    @Transactional
    public CourseDto updateCourseStatus(Integer courseId, CourseStatus status) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học"));
        course.setStatus(status);
        return mapToDto(courseRepository.save(course));
    }

    @Override
    @Transactional
    public void deleteCourse(Integer courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học"));
        courseRepository.delete(course);
    }

    private CourseDto mapToDto(Course course) {
        User teacher = course.getTeacher();
        UserDto teacherDto = UserDto.builder()
                .userId(teacher.getUserId())
                .username(teacher.getUsername())
                .email(teacher.getEmail())
                .fullName(teacher.getFullName())
                .role(teacher.getRole())
                .isActive(teacher.getIsActive())
                .build();

        return CourseDto.builder()
                .courseId(course.getCourseId())
                .title(course.getTitle())
                .description(course.getDescription())
                .price(course.getPrice())
                .durationHours(course.getDurationHours())
                .status(course.getStatus())
                .teacher(teacherDto)
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }
}
