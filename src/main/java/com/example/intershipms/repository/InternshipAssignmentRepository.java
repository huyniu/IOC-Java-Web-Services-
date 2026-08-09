package com.example.intershipms.repository;

import com.example.intershipms.entity.InternshipAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InternshipAssignmentRepository extends JpaRepository<InternshipAssignment, Integer> {
}