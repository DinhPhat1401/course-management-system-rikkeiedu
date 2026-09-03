package edu.rk.camel.coursemanagementsystem.repository;

import edu.rk.camel.coursemanagementsystem.model.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Integer> {
    List<Lesson> findByCourse_CourseId(Integer courseId);
    List<Lesson> findByCourse_CourseIdAndIsPublished(Integer courseId, Boolean isPublished);
}
