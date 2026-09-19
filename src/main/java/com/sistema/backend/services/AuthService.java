package com.sistema.backend.services;

import com.sistema.backend.dto.AuthResponse;
import com.sistema.backend.dto.LoginRequest;
import com.sistema.backend.models.User;
import com.sistema.backend.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }
        // Hasheo BCrypt antes de guardar en base de datos
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        userRepository.save(user);
        return "Usuario registrado exitosamente";
    }

    public AuthResponse login(LoginRequest request) {
        Optional<User> user = userRepository.findByUsername(request.getUsername());
        
        if (user.isPresent() && passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
            String token = jwtService.generateToken(user.get().getUsername());
            return new AuthResponse(token);
        }
        
        throw new RuntimeException("Credenciales inválidas");
    }
}