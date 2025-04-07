package com.user.services;

import com.user.exceptions.ResourceNotFound;
import com.user.model.Role;
import com.user.model.UserEntity;
import com.user.payload.LoginRequestDTO;
import com.user.repository.RoleRepository;
import com.user.repository.UserRepository;
import com.user.utils.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtils;
    private final RoleRepository roleRepository;

    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtils, RoleRepository roleRepository ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.roleRepository = roleRepository;
    }

    public String login(UserEntity user){
//        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
//        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtUtils.generateTokenFromUsername(user.getUsername());
    }

    public UserEntity register(LoginRequestDTO user){
        if(userRepository.existsByUsername(user.getUsername())){
            throw new ResourceNotFound("User with username " + user.getUsername() + " already exists");
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(user.getUsername());
        userEntity.setPassword(passwordEncoder.encode(user.getPassword()));

        Set<Role> roles = new HashSet<>();
        Role userRole;

        if(userRepository.count() == 0){
            userRole = roleRepository.findByName("ROLE_ADMIN");
            roles.add(roleRepository.findByName("ROLE_ADMIN"));
        } else {
            userRole = roleRepository.findByName("ROLE_"+user.getRole());
            roles.add(roleRepository.findByName(userRole.getName()));
        }
        roles.add(userRole);
        userEntity.setRoles(roles);

        return userRepository.save(userEntity);
    }
}
