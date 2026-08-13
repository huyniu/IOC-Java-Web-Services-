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

import com.example.intershipms.security.UserDetailsImpl;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MentorServiceImpl implements MentorService {

    private final MentorRepository mentorRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new AccessDeniedException("Bạn chưa đăng nhập hoặc phiên làm việc không hợp lệ!");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsImpl userDetails) {
            return userRepository.findById(userDetails.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin tài khoản!"));
        } else if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
            return userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin tài khoản!"));
        } else if (principal instanceof String username) {
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin tài khoản!"));
        } else {
            throw new AccessDeniedException("Bạn chưa đăng nhập hoặc phiên làm việc không hợp lệ!");
        }
    }

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
        // 1. Tìm hồ sơ giáo viên trong DB theo ID từ URL
        Mentor mentor = mentorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ giáo viên với ID: " + id));

        // 2. Lấy thông tin User đang đăng nhập
        User currentUser = getCurrentUser();

        // 3. Kiểm tra rẽ nhánh theo Role
        if (currentUser.getRole() == User.Role.MENTOR) {
            // Nếu là MENTOR: Bắt buộc phải trùng ID tài khoản đăng nhập với mentor_id từ URL
            if (!currentUser.getUserId().equals(mentor.getMentorId())) {
                throw new AccessDeniedException("Bạn không có quyền xem thông tin hồ sơ của giáo viên khác!");
            }
        }
        // ADMIN và STUDENT được phép xem chi tiết của tất cả giáo viên

        return mapToResponse(mentor, mentor.getUser());
    }

    @Override
    public MentorResponse updateMentor(Integer id, MentorUpdateRequest request) {
        // 1. Tìm hồ sơ giáo viên trong DB theo ID từ URL
        Mentor mentor = mentorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ giáo viên với ID: " + id));

        // 2. Lấy thông tin User đang đăng nhập từ SecurityContextHolder
        User currentUser = getCurrentUser();

        // 3. Kiểm tra rẽ nhánh quyền sửa đổi dữ liệu (Chống lỗi IDOR)
        if (currentUser.getRole() == User.Role.MENTOR) {
            // Nếu là MENTOR: Bắt buộc ID tài khoản đăng nhập phải trùng với ID giáo viên cần sửa
            if (!currentUser.getUserId().equals(mentor.getMentorId())) {
                throw new AccessDeniedException("Bạn không có quyền chỉnh sửa thông tin hồ sơ của giáo viên khác!");
            }
        }
        // Role ADMIN được phép cập nhật thông tin của tất cả giáo viên

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