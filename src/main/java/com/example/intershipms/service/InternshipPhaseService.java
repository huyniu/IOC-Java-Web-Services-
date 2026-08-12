package com.example.intershipms.service;

import com.example.intershipms.dto.request.InternshipPhaseRequest;
import com.example.intershipms.dto.request.InternshipPhaseUpdateRequest;
import com.example.intershipms.dto.response.InternshipPhaseResponse;

import java.util.List;

public interface InternshipPhaseService {
    InternshipPhaseResponse createPhase(InternshipPhaseRequest request);
    List<InternshipPhaseResponse> getAllPhases();
    InternshipPhaseResponse getPhaseById(Integer id);
    InternshipPhaseResponse updatePhase(Integer id, InternshipPhaseUpdateRequest request);
    void deletePhase(Integer id);
}
