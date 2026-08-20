package com.example.intershipms.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "internship_phases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternshipPhase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "phase_id")
    private Integer phaseId;

    @Column(name = "phase_name", length = 100, unique = true, nullable = false)
    private String phaseName;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    // Fix: đổi "EndDate" (PascalCase) → "end_date" (snake_case) nhất quán với PostgreSQL
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    // Fix: NVARCHAR(MAX) là kiểu của SQL Server, không hợp lệ với PostgreSQL.
    // Dùng TEXT — kiểu chuỗi không giới hạn độ dài chuẩn của PostgreSQL.
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}