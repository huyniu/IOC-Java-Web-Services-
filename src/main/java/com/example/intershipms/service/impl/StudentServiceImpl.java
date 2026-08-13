package com.example.intershipms.service.impl;

import com.example.intershipms.dto.request.StudentRequest;
import com.example.intershipms.dto.request.StudentUpdateRequest;
import com.example.intershipms.dto.response.StudentResponse;
import com.example.intershipms.entity.Student;
import com.example.intershipms.entity.User;
import com.example.intershipms.exception.BadRequestException;
import com.example.intershipms.exception.ResourceNotFoundException;
import com.example.intershipms.repository.StudentRepository;
import com.example.intershipms.repository.UserRepository;
import com.example.intershipms.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.example.intershipms.exception.ForbiddenOperationException;
import com.example.intershipms.security.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    @Override
    public StudentResponse createStudent(StudentRequest request) {
        // 1. Kiểm tra tài khoản User có tồn tại không
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản với ID: " + request.getUserId()));

        // 2. Ép buộc tài khoản này phải có Role là STUDENT
        if (user.getRole() != User.Role.STUDENT) {
            throw new BadRequestException("Tài khoản này không có quyền Sinh viên (STUDENT)!");
        }

        // 3. Quan hệ 1-1: Kiểm tra xem User này đã có hồ sơ Sinh viên chưa
        if (studentRepository.existsById(request.getUserId())) {
            throw new BadRequestException("Tài khoản này đã được liên kết với một hồ sơ Sinh viên!");
        }

        // 4. Kiểm tra trùng lặp Mã sinh viên
        if (studentRepository.existsByStudentCode(request.getStudentCode())) {
            throw new BadRequestException("Mã sinh viên đã tồn tại trong hệ thống!");
        }

        // 5. Nếu vượt qua mọi bài test, tiến hành tạo và lưu hồ sơ
        Student student = Student.builder()
                .user(user)
                .studentCode(request.getStudentCode())
                .major(request.getMajor())
                .className(request.getClassName())
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress())
                .build();

        Student savedStudent = studentRepository.save(student);
        return mapToResponse(savedStudent, user);
    }

    @Override
    public List<StudentResponse> getAllStudents() {
        // 1. Trích xuất thông tin User đang đăng nhập từ SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ForbiddenOperationException("Bạn chưa đăng nhập hoặc phiên làm việc không hợp lệ!");
        }

        Object principal = authentication.getPrincipal();
        User currentUser;

        if (principal instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) principal;
            currentUser = userRepository.findById(userDetails.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin tài khoản!"));
        } else if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            org.springframework.security.core.userdetails.UserDetails userDetails =
                    (org.springframework.security.core.userdetails.UserDetails) principal;
            currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin tài khoản!"));
        } else if (principal instanceof String) {
            String username = (String) principal;
            currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin tài khoản!"));
        } else {
            throw new ForbiddenOperationException("Bạn chưa đăng nhập hoặc phiên làm việc không hợp lệ!");
        }

        // 2. Kiểm tra Role và lọc danh sách sinh viên tương ứng
        List<Student> students;
        if (currentUser.getRole() == User.Role.ADMIN) {
            // Nếu Role là ADMIN: Lấy toàn bộ sinh viên
            students = studentRepository.findAll();
        } else if (currentUser.getRole() == User.Role.MENTOR) {
            // Nếu Role là MENTOR: Chỉ lấy danh sách sinh viên phân công cho Mentor này
            students = studentRepository.findStudentsByMentorId(currentUser.getUserId());
        } else if (currentUser.getRole() == User.Role.STUDENT) {
            // Nếu Role là STUDENT: Chặn truy cập và ném lỗi 403 Forbidden
            throw new ForbiddenOperationException("Sinh viên không có quyền truy cập danh sách sinh viên!");
        } else {
            throw new ForbiddenOperationException("Vai trò người dùng không hợp lệ!");
        }

        return students.stream()
                .map(student -> mapToResponse(student, student.getUser()))
                .collect(Collectors.toList());
    }

    // Hàm tiện ích: Trộn dữ liệu từ bảng Student và bảng User để trả về DTO
    private StudentResponse mapToResponse(Student student, User user) {
        return StudentResponse.builder()
                .studentId(student.getStudentId())
                .studentCode(student.getStudentCode())
                .major(student.getMajor())
                .className(student.getClassName())
                .dateOfBirth(student.getDateOfBirth())
                .address(student.getAddress())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    @Override
    public StudentResponse getStudentById(Integer id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ sinh viên với ID: " + id));
        return mapToResponse(student, student.getUser());
    }

    @Override
    public StudentResponse updateStudent(Integer id, StudentUpdateRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ sinh viên với ID: " + id));

        student.setMajor(request.getMajor());
        student.setClassName(request.getClassName());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setAddress(request.getAddress());

        Student updatedStudent = studentRepository.save(student);
        return mapToResponse(updatedStudent, updatedStudent.getUser());
    }

    @Override
    public void deleteStudent(Integer id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ sinh viên với ID: " + id));
        studentRepository.delete(student);
    }
}