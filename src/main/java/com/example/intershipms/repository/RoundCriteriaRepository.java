package com.example.intershipms.repository;

import com.example.intershipms.entity.RoundCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoundCriteriaRepository extends JpaRepository<RoundCriteria, Integer> {
    @Query("SELECT rc FROM RoundCriteria rc WHERE rc.round.roundId = :roundId")
    List<RoundCriteria> findByRoundId(@Param("roundId") Long roundId);

    List<RoundCriteria> findByRoundRoundId(Integer roundId);
    Optional<RoundCriteria> findByRoundRoundIdAndCriterionCriterionId(Integer roundId, Integer criterionId);
    boolean existsByRoundRoundIdAndCriterionCriterionId(Integer roundId, Integer criterionId);
}