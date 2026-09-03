package edu.rk.camel.coursemanagementsystem.repository;

import edu.rk.camel.coursemanagementsystem.model.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Integer> {
    List<LessonProgress> findByEnrollment_EnrollmentId(Integer enrollmentId);
    Optional<LessonProgress> findByEnrollment_EnrollmentIdAndLesson_LessonId(Integer enrollmentId, Integer lessonId);
    long countByEnrollment_EnrollmentIdAndIsCompletedTrue(Integer enrollmentId);
}
