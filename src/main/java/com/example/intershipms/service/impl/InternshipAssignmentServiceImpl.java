package com.example.intershipms.service.impl;

import com.example.intershipms.dto.request.AssignmentStatusUpdateRequest;
import com.example.intershipms.dto.request.InternshipAssignmentRequest;
import com.example.intershipms.dto.response.InternshipAssignmentResponse;
import com.example.intershipms.entity.InternshipAssignment;
import com.example.intershipms.entity.InternshipAssignment.AssignmentStatus;
import com.example.intershipms.entity.InternshipPhase;
import com.example.intershipms.entity.Mentor;
import com.example.intershipms.entity.Student;
import com.example.intershipms.exception.BadRequestException;
import com.example.intershipms.exception.ResourceNotFoundException;
import com.example.intershipms.repository.InternshipAssignmentRepository;
import com.example.intershipms.repository.InternshipPhaseRepository;
import com.example.intershipms.repository.MentorRepository;
import com.example.intershipms.repository.StudentRepository;
import com.example.intershipms.service.InternshipAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InternshipAssignmentServiceImpl implements InternshipAssignmentService {

    private final InternshipAssignmentRepository assignmentRepository;
    private final StudentRepository studentRepository;
    private final MentorRepository mentorRepository;
    private final InternshipPhaseRepository phaseRepository;

    @Override
    public InternshipAssignmentResponse assignStudent(InternshipAssignmentRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sinh viên với ID: " + request.getStudentId()));

        Mentor mentor = mentorRepository.findById(request.getMentorId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên hướng dẫn với ID: " + request.getMentorId()));

        InternshipPhase phase = phaseRepository.findById(request.getPhaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giai đoạn thực tập với ID: " + request.getPhaseId()));

        if (assignmentRepository.existsByStudentStudentIdAndPhasePhaseId(request.getStudentId(), request.getPhaseId())) {
            throw new BadRequestException("Sinh viên đã được phân công thực tập trong giai đoạn này!");
        }

        InternshipAssignment assignment = InternshipAssignment.builder()
                .student(student)
                .mentor(mentor)
                .phase(phase)
                .assignedDate(LocalDateTime.now())
                .status(request.getStatus() != null ? request.getStatus() : AssignmentStatus.PENDING)
                .build();

        InternshipAssignment savedAssignment = assignmentRepository.save(assignment);
        return mapToResponse(savedAssignment);
    }

    @Override
    public List<InternshipAssignmentResponse> getAllAssignments() {
        return assignmentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public InternshipAssignmentResponse getAssignmentById(Integer id) {
        InternshipAssignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phân công thực tập với ID: " + id));
        return mapToResponse(assignment);
    }

    @Override
    public List<InternshipAssignmentResponse> getAssignmentsByMentorId(Integer mentorId) {
        if (!mentorRepository.existsById(mentorId)) {
            throw new ResourceNotFoundException("Không tìm thấy giảng viên hướng dẫn với ID: " + mentorId);
        }
        return assignmentRepository.findByMentorMentorId(mentorId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<InternshipAssignmentResponse> getAssignmentsByStudentId(Integer studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Không tìm thấy sinh viên với ID: " + studentId);
        }
        return assignmentRepository.findByStudentStudentId(studentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public InternshipAssignmentResponse updateAssignmentStatus(Integer id, AssignmentStatusUpdateRequest request) {
        InternshipAssignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phân công thực tập với ID: " + id));

        assignment.setStatus(request.getStatus());
        InternshipAssignment updatedAssignment = assignmentRepository.save(assignment);
        return mapToResponse(updatedAssignment);
    }

    private InternshipAssignmentResponse mapToResponse(InternshipAssignment assignment) {
        return InternshipAssignmentResponse.builder()
                .assignmentId(assignment.getAssignmentId())
                .studentId(assignment.getStudent().getStudentId())
                .studentName(assignment.getStudent().getUser().getFullName())
                .studentCode(assignment.getStudent().getStudentCode())
                .mentorId(assignment.getMentor().getMentorId())
                .mentorName(assignment.getMentor().getUser().getFullName())
                .phaseId(assignment.getPhase().getPhaseId())
                .phaseName(assignment.getPhase().getPhaseName())
                .assignedDate(assignment.getAssignedDate())
                .status(assignment.getStatus())
                .build();
    }
}
