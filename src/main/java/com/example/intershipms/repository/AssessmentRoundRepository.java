package com.example.intershipms.repository;

import com.example.intershipms.entity.AssessmentRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentRoundRepository extends JpaRepository<AssessmentRound, Integer> {
    List<AssessmentRound> findByPhasePhaseId(Integer phaseId);
    boolean existsByPhasePhaseIdAndRoundName(Integer phaseId, String roundName);
    boolean existsByPhasePhaseIdAndRoundNameAndRoundIdNot(Integer phaseId, String roundName, Integer roundId);
}