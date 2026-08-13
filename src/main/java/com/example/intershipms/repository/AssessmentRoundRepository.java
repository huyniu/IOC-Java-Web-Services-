package com.example.intershipms.repository;

import com.example.intershipms.entity.AssessmentRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentRoundRepository extends JpaRepository<AssessmentRound, Integer> {
    @Query("SELECT r FROM AssessmentRound r WHERE r.phase.phaseId = :phaseId")
    List<AssessmentRound> findByPhaseId(@Param("phaseId") Long phaseId);

    List<AssessmentRound> findByPhasePhaseId(Integer phaseId);
    boolean existsByPhasePhaseIdAndRoundName(Integer phaseId, String roundName);
    boolean existsByPhasePhaseIdAndRoundNameAndRoundIdNot(Integer phaseId, String roundName, Integer roundId);
}