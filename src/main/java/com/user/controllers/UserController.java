package com.user.controllers;

import com.user.model.UserEntity;
import com.user.services.UserService;
import com.user.utils.JwtUtil;
import com.user.utils.S3FileStorageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/app/v1")
public class UserController {

    private final UserService userService;
    private final S3FileStorageUtils s3FileStorageUtils;
    public UserController(UserService userService, S3FileStorageUtils s3FileStorageUtils) {
        this.userService = userService;
        this.s3FileStorageUtils = s3FileStorageUtils;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public List<UserEntity> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/upload-image/{id}")
    public ResponseEntity<Map<String, Object>> uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        String loggedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity loggedUser = userService.getUserById(id);

        if(!loggedUser.getUsername().equals(loggedUsername)) {
            response.put("message", "You are not the owner of this user");
            response.put("status", HttpStatus.UNAUTHORIZED);
            return ResponseEntity.ok(response);
        }

        String filePath = s3FileStorageUtils.uploadFile(file);
        UserEntity user = userService.getUserById(id);
        user.setUsername(user.getUsername());
        user.setPassword(user.getPassword());
        user.setImgUrl(filePath);
        userService.saveUser(user);


        response.put("status", "success");
        response.put("user", user);

        return ResponseEntity.ok(response);
    }
}
