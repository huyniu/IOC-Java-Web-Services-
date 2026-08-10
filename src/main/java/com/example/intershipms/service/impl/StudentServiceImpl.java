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
        return studentRepository.findAll().stream()
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