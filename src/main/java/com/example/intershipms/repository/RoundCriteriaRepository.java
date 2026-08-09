package com.example.intershipms.repository;

import com.example.intershipms.entity.RoundCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoundCriteriaRepository extends JpaRepository<RoundCriteria, Integer> {
}