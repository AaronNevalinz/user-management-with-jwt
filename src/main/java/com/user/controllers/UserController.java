package com.user.controllers;

import com.user.model.UserEntity;
import com.user.services.UserService;
import com.user.utils.JwtUtil;
import com.user.utils.S3FileStorageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private AuthenticationManager authenticationManager;
    private final UserService userService;
    private final S3FileStorageUtils s3FileStorageUtils;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    public UserController(UserService userService, S3FileStorageUtils s3FileStorageUtils, PasswordEncoder passwordEncoder,  UserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.s3FileStorageUtils = s3FileStorageUtils;
        this.passwordEncoder = passwordEncoder;
//        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public UserEntity register(@RequestBody UserEntity user) {
        UserEntity newUser = new UserEntity();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        return userService.saveUser(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserEntity user) {
        Authentication authentication;
        try{
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        }catch (AuthenticationException e){
            Map<String, Object> map = new HashMap<>();
            map.put("error", "Invalid username or password");
            map.put("status", 401);
            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
        }

//        Generate JWT token
        String token = jwtUtil.generateTokenFromUsername(user.getUsername());

        Map<String, Object> map = new HashMap<>();
        map.put("status", 200);
        map.put("user", user.getUsername());
        map.put("token", token);

        return ResponseEntity.ok(map);

    }

    @GetMapping("/users")
    public List<UserEntity> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/upload-image/{id}")
    public ResponseEntity<Map<String, Object>> uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        String filePath = s3FileStorageUtils.uploadFile(file);
        UserEntity user = userService.getUserById(id);
        user.setUsername(user.getUsername());
        user.setPassword(user.getPassword());
        user.setImgUrl(filePath);
        userService.saveUser(user);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("user", user);
        return ResponseEntity.ok(response);
    }
}
