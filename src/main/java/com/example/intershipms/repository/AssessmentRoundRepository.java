package com.example.intershipms.repository;

import com.example.intershipms.entity.AssessmentRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssessmentRoundRepository extends JpaRepository<AssessmentRound, Integer> {
}