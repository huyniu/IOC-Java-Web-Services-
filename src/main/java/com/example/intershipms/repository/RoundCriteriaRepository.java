package com.example.intershipms.repository;

import com.example.intershipms.entity.RoundCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoundCriteriaRepository extends JpaRepository<RoundCriteria, Integer> {
    List<RoundCriteria> findByRoundRoundId(Integer roundId);
    Optional<RoundCriteria> findByRoundRoundIdAndCriterionCriterionId(Integer roundId, Integer criterionId);
    boolean existsByRoundRoundIdAndCriterionCriterionId(Integer roundId, Integer criterionId);
}