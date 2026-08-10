package com.example.intershipms.service;

import com.example.intershipms.dto.request.MentorRequest;
import com.example.intershipms.dto.request.MentorUpdateRequest;
import com.example.intershipms.dto.response.MentorResponse;
import java.util.List;

public interface MentorService {
    MentorResponse createMentor(MentorRequest request);
    List<MentorResponse> getAllMentors();
    MentorResponse getMentorById(Integer id);
    MentorResponse updateMentor(Integer id, MentorUpdateRequest request);
    void deleteMentor(Integer id);
}