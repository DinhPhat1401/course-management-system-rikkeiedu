package edu.rk.camel.coursemanagementsystem.repository;

import edu.rk.camel.coursemanagementsystem.model.entity.Course;
import edu.rk.camel.coursemanagementsystem.model.entity.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    List<Course> findByStatus(CourseStatus status);
    List<Course> findByTeacher_UserId(Integer teacherId);
    List<Course> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);

    @org.springframework.data.jpa.repository.Query("SELECT c, COUNT(e) as enrollCount FROM Course c LEFT JOIN Enrollment e ON c.courseId = e.course.courseId GROUP BY c.courseId ORDER BY enrollCount DESC")
    List<Object[]> findTopCoursesByEnrollments(org.springframework.data.domain.Pageable pageable);
    
    long countByTeacher_UserId(Integer teacherId);
}
