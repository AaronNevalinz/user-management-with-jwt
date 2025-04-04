package com.user.controllers;

import com.user.model.UserEntity;
import com.user.payload.LoginResponseDto;
import com.user.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = {"/login", "/signin"})
    public ResponseEntity<LoginResponseDto> login(@RequestBody UserEntity user) {
        String token = authService.login(user);
        LoginResponseDto response = new LoginResponseDto(user.getUsername(), token);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = {"/register", "/signup"})
    public ResponseEntity<UserEntity> register(@RequestBody UserEntity user) {
        UserEntity registerUser = authService.register(user);
        return ResponseEntity.ok(registerUser);
    }
}
