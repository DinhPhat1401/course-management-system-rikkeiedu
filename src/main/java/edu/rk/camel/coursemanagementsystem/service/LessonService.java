package edu.rk.camel.coursemanagementsystem.service;

import edu.rk.camel.coursemanagementsystem.model.dto.request.LessonCreateRequest;
import edu.rk.camel.coursemanagementsystem.model.dto.request.LessonUpdateRequest;
import edu.rk.camel.coursemanagementsystem.model.dto.response.LessonDto;

import java.util.List;

public interface LessonService {
    List<LessonDto> getLessonsByCourse(Integer courseId, boolean onlyPublished);
    LessonDto getLessonById(Integer lessonId);
    LessonDto createLesson(Integer courseId, LessonCreateRequest request);
    LessonDto updateLesson(Integer lessonId, LessonUpdateRequest request);
    LessonDto updateLessonStatus(Integer lessonId, Boolean isPublished);
    void deleteLesson(Integer lessonId);
    String getLessonContentPreview(Integer lessonId);
}
