package edu.rk.camel.coursemanagementsystem.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lesson_progress", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"enrollment_id", "lesson_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer progressId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isCompleted = false;

    private LocalDateTime completedAt;

    @Column(nullable = false)
    private LocalDateTime lastAccessedAt;

    @PrePersist
    protected void onCreate() {
        lastAccessedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastAccessedAt = LocalDateTime.now();
    }
}
