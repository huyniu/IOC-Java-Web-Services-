package com.example.intershipms.repository;

import com.example.intershipms.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    boolean existsByStudentCode(String studentCode);
}