package com.example.intershipms.repository;

import com.example.intershipms.entity.InternshipAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InternshipAssignmentRepository extends JpaRepository<InternshipAssignment, Integer> {
    List<InternshipAssignment> findByMentorMentorId(Integer mentorId);
    List<InternshipAssignment> findByStudentStudentId(Integer studentId);
    Optional<InternshipAssignment> findByStudentStudentIdAndPhasePhaseId(Integer studentId, Integer phaseId);
    boolean existsByStudentStudentIdAndPhasePhaseId(Integer studentId, Integer phaseId);
    boolean existsByPhasePhaseId(Integer phaseId);
}