package com.example.intershipms.repository;

import com.example.intershipms.entity.InternshipPhase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InternshipPhaseRepository extends JpaRepository<InternshipPhase, Integer> {
    boolean existsByPhaseName(String phaseName);
    boolean existsByPhaseNameAndPhaseIdNot(String phaseName, Integer phaseId);
}