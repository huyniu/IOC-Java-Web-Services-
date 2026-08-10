package com.example.intershipms.service.impl;

import com.example.intershipms.dto.request.MentorRequest;
import com.example.intershipms.dto.request.MentorUpdateRequest;
import com.example.intershipms.dto.response.MentorResponse;
import com.example.intershipms.entity.Mentor;
import com.example.intershipms.entity.User;
import com.example.intershipms.exception.BadRequestException;
import com.example.intershipms.exception.ResourceNotFoundException;
import com.example.intershipms.repository.MentorRepository;
import com.example.intershipms.repository.UserRepository;
import com.example.intershipms.service.MentorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MentorServiceImpl implements MentorService {

    private final MentorRepository mentorRepository;
    private final UserRepository userRepository;

    @Override
    public MentorResponse createMentor(MentorRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản với ID: " + request.getUserId()));

        if (user.getRole() != User.Role.MENTOR) {
            throw new BadRequestException("Tài khoản này không có quyền Giáo viên (MENTOR)!");
        }

        if (mentorRepository.existsById(request.getUserId())) {
            throw new BadRequestException("Tài khoản này đã được liên kết với một hồ sơ Giáo viên!");
        }

        Mentor mentor = Mentor.builder()
                .user(user)
                .department(request.getDepartment())
                .academicRank(request.getAcademicRank())
                .build();

        Mentor savedMentor = mentorRepository.save(mentor);
        return mapToResponse(savedMentor, user);
    }

    @Override
    public List<MentorResponse> getAllMentors() {
        return mentorRepository.findAll().stream()
                .map(mentor -> mapToResponse(mentor, mentor.getUser()))
                .collect(Collectors.toList());
    }

    @Override
    public MentorResponse getMentorById(Integer id) {
        Mentor mentor = mentorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ giáo viên với ID: " + id));
        return mapToResponse(mentor, mentor.getUser());
    }

    @Override
    public MentorResponse updateMentor(Integer id, MentorUpdateRequest request) {
        Mentor mentor = mentorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ giáo viên với ID: " + id));

        mentor.setDepartment(request.getDepartment());
        mentor.setAcademicRank(request.getAcademicRank());

        Mentor updatedMentor = mentorRepository.save(mentor);
        return mapToResponse(updatedMentor, updatedMentor.getUser());
    }

    @Override
    public void deleteMentor(Integer id) {
        Mentor mentor = mentorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ giáo viên với ID: " + id));
        mentorRepository.delete(mentor);
    }

    private MentorResponse mapToResponse(Mentor mentor, User user) {
        return MentorResponse.builder()
                .mentorId(mentor.getMentorId())
                .department(mentor.getDepartment())
                .academicRank(mentor.getAcademicRank())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}