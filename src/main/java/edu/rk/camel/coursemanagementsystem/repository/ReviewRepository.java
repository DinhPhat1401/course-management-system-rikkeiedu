package edu.rk.camel.coursemanagementsystem.repository;

import edu.rk.camel.coursemanagementsystem.model.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByCourse_CourseId(Integer courseId);
    Optional<Review> findByCourse_CourseIdAndStudent_UserId(Integer courseId, Integer studentId);

    @org.springframework.data.jpa.repository.Query("SELECT AVG(r.rating) FROM Review r WHERE r.course.teacher.userId = :teacherId")
    Double getAverageRatingForTeacher(@org.springframework.data.repository.query.Param("teacherId") Integer teacherId);
}
