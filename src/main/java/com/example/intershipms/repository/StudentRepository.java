package com.example.intershipms.repository;

import com.example.intershipms.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    boolean existsByStudentCode(String studentCode);

    @Query("SELECT DISTINCT ia.student FROM InternshipAssignment ia WHERE ia.mentor.mentorId = :mentorId")
    List<Student> findStudentsByMentorId(@Param("mentorId") Integer mentorId);
}