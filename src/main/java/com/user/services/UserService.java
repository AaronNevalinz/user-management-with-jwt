package com.user.services;

import com.user.exceptions.ResourceNotFound;
import com.user.model.UserEntity;
import com.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity saveUser(UserEntity user) {
        return userRepository.save(user);
    }


    public UserEntity getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(()->new ResourceNotFound("user not found"));
    }

    public List<UserEntity> getAllUsers(){
        return userRepository.findAll();
    }
}
