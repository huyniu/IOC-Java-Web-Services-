package com.example.intershipms.repository;

import com.example.intershipms.entity.AssessmentResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssessmentResultRepository extends JpaRepository<AssessmentResult, Integer> {
    List<AssessmentResult> findByAssignmentAssignmentId(Integer assignmentId);
    List<AssessmentResult> findByAssignmentStudentStudentIdAndAssignmentPhasePhaseId(Integer studentId, Integer phaseId);
    Optional<AssessmentResult> findByAssignmentAssignmentIdAndRoundRoundIdAndCriterionCriterionId(Integer assignmentId, Integer roundId, Integer criterionId);
}