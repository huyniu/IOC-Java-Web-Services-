package com.example.intershipms.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "AssessmentRounds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentRound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RoundID")
    private Integer roundId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PhaseID", nullable = false)
    private InternshipPhase phase;

    @Column(name = "RoundName", length = 100, nullable = false)
    private String roundName;

    @Column(name = "StartDate", nullable = false)
    private LocalDate startDate;

    @Column(name = "EndDate", nullable = false)
    private LocalDate endDate;

    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "IsActive")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "CreatedAt", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;
}