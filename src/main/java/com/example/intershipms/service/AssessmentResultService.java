package com.example.intershipms.service;

import com.example.intershipms.dto.request.AssessmentResultRequest;
import com.example.intershipms.dto.response.AssessmentResultResponse;
import com.example.intershipms.dto.response.StudentEvaluationSummaryResponse;

import java.util.List;

public interface AssessmentResultService {
    AssessmentResultResponse submitAssessment(AssessmentResultRequest request, String currentUsername);
    List<AssessmentResultResponse> getAllResults();
    AssessmentResultResponse getResultById(Integer id);
    AssessmentResultResponse updateAssessment(Integer id, AssessmentResultRequest request, String currentUsername);
    List<AssessmentResultResponse> getResultsByAssignmentId(Integer assignmentId);
    StudentEvaluationSummaryResponse getStudentSummaryByPhase(Integer studentId, Integer phaseId);
}
