package com.example.intershipms.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "InternshipAssignments", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"StudentID", "PhaseID"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternshipAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AssignmentID")
    private Integer assignmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "StudentID", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MentorID", nullable = false)
    private Mentor mentor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PhaseID", nullable = false)
    private InternshipPhase phase;

    @CreationTimestamp
    @Column(name = "AssignedDate", updatable = false)
    private LocalDateTime assignedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status")
    private AssignmentStatus status = AssignmentStatus.PENDING;

    @CreationTimestamp
    @Column(name = "CreatedAt", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    public enum AssignmentStatus {
        PENDING, IN_PROGRESS, COMPLETED, FAILED
    }
}