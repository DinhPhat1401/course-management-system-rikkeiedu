package edu.rk.camel.coursemanagementsystem.repository;

import edu.rk.camel.coursemanagementsystem.model.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    List<Enrollment> findByStudent_UserId(Integer studentId);
    Optional<Enrollment> findByStudent_UserIdAndCourse_CourseId(Integer studentId, Integer courseId);

    @org.springframework.data.jpa.repository.Query("SELECT e.status, COUNT(e) FROM Enrollment e WHERE e.student.userId = :studentId GROUP BY e.status")
    List<Object[]> countStudentProgressByStatus(@org.springframework.data.repository.query.Param("studentId") Integer studentId);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.teacher.userId = :teacherId")
    long countTotalStudentsForTeacher(@org.springframework.data.repository.query.Param("teacherId") Integer teacherId);
}
