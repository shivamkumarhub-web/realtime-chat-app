package com.shivam.chat.service;

import com.shivam.chat.dto.JwtResponse;
import com.shivam.chat.dto.LoginRequest;
import com.shivam.chat.dto.RegisterRequest;
import com.shivam.chat.entity.User;
import com.shivam.chat.exception.UserAlreadyExistsException;
import com.shivam.chat.repository.UserRepository;
import com.shivam.chat.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository; this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider; this.authenticationManager = authenticationManager;
    }

    @Transactional
    public JwtResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new UserAlreadyExistsException("Username already taken");
        if (userRepository.existsByEmail(request.getEmail()))
            throw new UserAlreadyExistsException("Email already registered");
        User user = new User(request.getUsername(), request.getEmail(), passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of("USER"));
        userRepository.save(user);
        String token = tokenProvider.generateTokenFromUsername(user.getUsername(), user.getRoles());
        return new JwtResponse(token, "Bearer", user.getUsername(), user.getRoles());
    }

    public JwtResponse login(LoginRequest request) {
        org.springframework.security.core.Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        String token = tokenProvider.generateToken(auth);
        return new JwtResponse(token, "Bearer", user.getUsername(), user.getRoles());
    }
}