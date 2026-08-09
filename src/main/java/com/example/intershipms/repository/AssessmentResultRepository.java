package com.example.intershipms.repository;

import com.example.intershipms.entity.AssessmentResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssessmentResultRepository extends JpaRepository<AssessmentResult, Integer> {
}