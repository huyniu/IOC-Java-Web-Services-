package com.example.intershipms.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "RoundCriteria", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"RoundID", "CriterionID"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoundCriteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RoundCriterionID")
    private Integer roundCriterionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RoundID", nullable = false)
    private AssessmentRound round;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CriterionID", nullable = false)
    private EvaluationCriteria criterion;

    @Column(name = "Weight", precision = 5, scale = 2, nullable = false)
    private BigDecimal weight;

    @CreationTimestamp
    @Column(name = "CreatedAt", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;
}