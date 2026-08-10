package com.example.intershipms.service;

import com.example.intershipms.dto.request.StudentRequest;
import com.example.intershipms.dto.request.StudentUpdateRequest;
import com.example.intershipms.dto.response.StudentResponse;
import java.util.List;

public interface StudentService {
    StudentResponse createStudent(StudentRequest request);
    List<StudentResponse> getAllStudents();
    StudentResponse getStudentById(Integer id);
    StudentResponse updateStudent(Integer id, StudentUpdateRequest request);
    void deleteStudent(Integer id);
}