package edu.rk.camel.coursemanagementsystem.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "course_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer enrollmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false, updatable = false)
    private LocalDateTime enrollmentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EnrollmentStatus status = EnrollmentStatus.ENROLLED;

    private LocalDateTime completionDate;

    @Column(nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal progressPercentage = BigDecimal.ZERO;

    @PrePersist
    protected void onCreate() {
        enrollmentDate = LocalDateTime.now();
    }
}
