package com.example.intershipms.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "AssessmentResults", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"AssignmentID", "RoundID", "CriterionID"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ResultID")
    private Integer resultId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AssignmentID", nullable = false)
    private InternshipAssignment assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RoundID", nullable = false)
    private AssessmentRound round;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CriterionID", nullable = false)
    private EvaluationCriteria criterion;

    @Column(name = "Score", nullable = false, precision = 5, scale = 2)
    private BigDecimal score;

    @Column(name = "Comments", columnDefinition = "TEXT")
    private String comments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EvaluatedBy", nullable = false)
    private User evaluatedBy;

    @CreationTimestamp
    @Column(name = "EvaluationDate", updatable = false)
    private LocalDateTime evaluationDate;

    @CreationTimestamp
    @Column(name = "CreatedAt", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;
}