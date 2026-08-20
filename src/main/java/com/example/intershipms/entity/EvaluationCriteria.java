package com.example.intershipms.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
// Fix #1: Đổi "EvaluationCriteria" (PascalCase) → "evaluation_criteria" (snake_case)
// PostgreSQL mặc định lowercase tên bảng → "evaluationcriteria" != "evaluation_criteria"
@Table(name = "evaluation_criteria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationCriteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Fix #2: "CriterionID" → "criterion_id" (snake_case chuẩn PostgreSQL)
    @Column(name = "criterion_id")
    private Integer criterionId;

    @Column(name = "criterion_name", length = 200, unique = true, nullable = false)
    private String criterionName;

    // Fix #3: NVARCHAR(MAX) là kiểu của SQL Server, KHÔNG hợp lệ trên PostgreSQL
    // → Dùng TEXT (chuỗi không giới hạn độ dài, chuẩn PostgreSQL)
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "max_score", precision = 5, scale = 2, nullable = false)
    private BigDecimal maxScore;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}