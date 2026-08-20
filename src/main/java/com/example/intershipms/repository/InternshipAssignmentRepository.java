package com.example.intershipms.repository;

import com.example.intershipms.entity.InternshipAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InternshipAssignmentRepository extends JpaRepository<InternshipAssignment, Integer> {
    // Tìm danh sách phân công thực tập theo ID của Mentor (người hướng dẫn)
    List<InternshipAssignment> findByMentorMentorId(Integer mentorId);

    // Lấy toàn bộ phân công thực tập của một bạn Sinh viên
    List<InternshipAssignment> findByStudentStudentId(Integer studentId);

    // Tim thông tin phân công cụ thể của 1 sinh viên trong 1 khóa/đợt thực tập
    Optional<InternshipAssignment> findByStudentStudentIdAndPhasePhaseId(Integer studentId, Integer phaseId);

    // Kiểm tra xem sinh viên này đã được xếp vào khóa thực tập này hay chưa
    boolean existsByStudentStudentIdAndPhasePhaseId(Integer studentId, Integer phaseId);

    // Kiểm tra xem khóa/đợt thực tập này hiện có sinh viên nào đang theo học hay không
    boolean existsByPhasePhaseId(Integer phaseId);
}