package com.example.intershipms.service;

import com.example.intershipms.dto.request.AssignmentStatusUpdateRequest;
import com.example.intershipms.dto.request.InternshipAssignmentRequest;
import com.example.intershipms.dto.response.InternshipAssignmentResponse;

import java.util.List;

public interface InternshipAssignmentService {
    InternshipAssignmentResponse assignStudent(InternshipAssignmentRequest request);
    List<InternshipAssignmentResponse> getAllAssignments();
    InternshipAssignmentResponse getAssignmentById(Integer id);
    List<InternshipAssignmentResponse> getAssignmentsByMentorId(Integer mentorId);
    List<InternshipAssignmentResponse> getAssignmentsByStudentId(Integer studentId);
    InternshipAssignmentResponse updateAssignmentStatus(Integer id, AssignmentStatusUpdateRequest request);
}
