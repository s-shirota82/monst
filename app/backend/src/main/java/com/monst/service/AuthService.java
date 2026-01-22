package com.monst.service;

import com.monst.dto.request.LoginRequest;
import com.monst.dto.response.LoginResponse;
import com.monst.repository.UserRepository;
import com.monst.service.exception.UnauthorizedException;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LoginResponse login(LoginRequest req) {
        var user = userRepository.findByEmail(req.email())
                .orElseThrow(UnauthorizedException::new);

        if (!passwordEncoder.matches(req.password(), user.password())) {
            throw new UnauthorizedException();
        }

        return new LoginResponse(user.id(), user.email());
    }
}
