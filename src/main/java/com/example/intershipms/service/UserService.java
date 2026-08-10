package com.example.intershipms.service;

import com.example.intershipms.dto.request.UserCreationRequest;
import com.example.intershipms.dto.request.UserRoleUpdateRequest;
import com.example.intershipms.dto.request.UserStatusUpdateRequest;
import com.example.intershipms.dto.request.UserUpdateRequest;
import com.example.intershipms.dto.response.UserResponse;
import com.example.intershipms.entity.User;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserCreationRequest request);
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Integer id);
    UserResponse getUserByUsername(String username);
    UserResponse updateUser(Integer id, UserUpdateRequest request);
    UserResponse updateUserStatus(Integer id, UserStatusUpdateRequest request);
    UserResponse updateUserRole(Integer id, UserRoleUpdateRequest request);
    void deleteUser(Integer id);
}