package com.example.intershipms.service;

import com.example.intershipms.dto.request.UserCreationRequest;
import com.example.intershipms.dto.response.UserResponse;
import java.util.List;

public interface UserService {
    UserResponse createUser(UserCreationRequest request);
    List<UserResponse> getAllUsers();
}