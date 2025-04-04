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

    public void deleteUserById(Long id) {
        try{
            UserEntity user = userRepository.findById(id).get();
            userRepository.delete(user);
        }catch (ResourceNotFound ex) {
            throw new ResourceNotFound(ex.getMessage());
        }
    }


    public UserEntity getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(()->new ResourceNotFound("user not found"));
    }

    public List<UserEntity> getAllUsers(){
        return userRepository.findAll();
    }
}
