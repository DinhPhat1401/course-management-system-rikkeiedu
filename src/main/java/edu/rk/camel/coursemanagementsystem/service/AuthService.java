package edu.rk.camel.coursemanagementsystem.service;

import edu.rk.camel.coursemanagementsystem.model.dto.request.LoginRequest;
import edu.rk.camel.coursemanagementsystem.model.dto.response.AuthResponse;
import edu.rk.camel.coursemanagementsystem.model.dto.response.UserDto;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    UserDto getCurrentUserProfile();
}
