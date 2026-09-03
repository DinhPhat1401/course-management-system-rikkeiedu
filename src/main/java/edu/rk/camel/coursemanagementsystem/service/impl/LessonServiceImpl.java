package edu.rk.camel.coursemanagementsystem.service.impl;

import edu.rk.camel.coursemanagementsystem.exception.ApiException;
import edu.rk.camel.coursemanagementsystem.exception.ResourceNotFoundException;
import edu.rk.camel.coursemanagementsystem.model.dto.request.LessonCreateRequest;
import edu.rk.camel.coursemanagementsystem.model.dto.request.LessonUpdateRequest;
import edu.rk.camel.coursemanagementsystem.model.dto.response.LessonDto;
import edu.rk.camel.coursemanagementsystem.model.entity.Course;
import edu.rk.camel.coursemanagementsystem.model.entity.Lesson;
import edu.rk.camel.coursemanagementsystem.model.entity.Role;
import edu.rk.camel.coursemanagementsystem.model.entity.User;
import edu.rk.camel.coursemanagementsystem.repository.CourseRepository;
import edu.rk.camel.coursemanagementsystem.repository.LessonRepository;
import edu.rk.camel.coursemanagementsystem.security.CustomUserDetails;
import edu.rk.camel.coursemanagementsystem.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;

    @Override
    public List<LessonDto> getLessonsByCourse(Integer courseId, boolean onlyPublished) {
        if (onlyPublished) {
            return lessonRepository.findByCourse_CourseIdAndIsPublished(courseId, true)
                    .stream().map(this::mapToDto).collect(Collectors.toList());
        }
        return lessonRepository.findByCourse_CourseId(courseId)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public LessonDto getLessonById(Integer lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học"));
        return mapToDto(lesson);
    }

    @Override
    @Transactional
    public LessonDto createLesson(Integer courseId, LessonCreateRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học"));

        checkTeacherPermission(course);

        Lesson lesson = Lesson.builder()
                .course(course)
                .title(request.getTitle())
                .textContent(request.getTextContent())
                .contentUrl(request.getContentUrl())
                .orderIndex(request.getOrderIndex())
                .isPublished(false) // Default draft
                .build();

        return mapToDto(lessonRepository.save(lesson));
    }

    @Override
    @Transactional
    public LessonDto updateLesson(Integer lessonId, LessonUpdateRequest request) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học"));

        checkTeacherPermission(lesson.getCourse());

        lesson.setTitle(request.getTitle());
        lesson.setTextContent(request.getTextContent());
        lesson.setContentUrl(request.getContentUrl());
        if (request.getOrderIndex() != null) lesson.setOrderIndex(request.getOrderIndex());

        return mapToDto(lessonRepository.save(lesson));
    }

    @Override
    @Transactional
    public LessonDto updateLessonStatus(Integer lessonId, Boolean isPublished) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học"));

        checkTeacherPermission(lesson.getCourse());

        lesson.setIsPublished(isPublished);
        return mapToDto(lessonRepository.save(lesson));
    }

    @Override
    @Transactional
    public void deleteLesson(Integer lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học"));

        checkTeacherPermission(lesson.getCourse());

        lessonRepository.delete(lesson);
    }

    @Override
    public String getLessonContentPreview(Integer lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học"));
        
        String content = lesson.getTextContent();
        if (content != null && content.length() > 100) {
            return content.substring(0, 100) + "...";
        }
        return content;
    }

    private void checkTeacherPermission(Course course) {
        CustomUserDetails currentUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = currentUser.getUser();

        if (user.getRole() == Role.ADMIN) {
            return;
        }
        if (user.getRole() == Role.TEACHER && !course.getTeacher().getUserId().equals(user.getUserId())) {
            throw new ApiException("ACCESS_DENIED", "Bạn không có quyền chỉnh sửa khóa học của người khác", 403);
        }
        if (user.getRole() == Role.STUDENT) {
            throw new ApiException("ACCESS_DENIED", "Sinh viên không có quyền thực hiện thao tác này", 403);
        }
    }

    private LessonDto mapToDto(Lesson lesson) {
        return LessonDto.builder()
                .lessonId(lesson.getLessonId())
                .courseId(lesson.getCourse().getCourseId())
                .title(lesson.getTitle())
                .textContent(lesson.getTextContent())
                .contentUrl(lesson.getContentUrl())
                .orderIndex(lesson.getOrderIndex())
                .isPublished(lesson.getIsPublished())
                .createdAt(lesson.getCreatedAt())
                .updatedAt(lesson.getUpdatedAt())
                .build();
    }
}
