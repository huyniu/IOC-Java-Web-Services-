package com.example.intershipms.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @Column(name = "StudentID")
    private Integer studentId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "StudentID")
    private User user;

    @Column(name = "StudentCode", length = 20, unique = true, nullable = false)
    private String studentCode;

    @Column(name = "Major", length = 100)
    private String major;

    @Column(name = "Class", length = 50)
    private String className;

    @Column(name = "DateOfBirth")
    private LocalDate dateOfBirth;

    @Column(name = "Address", length = 255)
    private String address;

    @CreationTimestamp
    @Column(name = "CreatedAt", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;
}