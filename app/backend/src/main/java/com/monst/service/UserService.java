package com.monst.service;

import com.monst.dto.request.RegisterRequest;
import com.monst.dto.response.RegisterResponse;
import com.monst.repository.UserRepository;
import com.monst.service.exception.EmailAlreadyUsedException;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public RegisterResponse register(RegisterRequest req) {
        if (userRepository.findByEmail(req.email()).isPresent()) {
            throw new EmailAlreadyUsedException();
        }

        String hash = passwordEncoder.encode(req.password());
        long id = userRepository.insert(req.email(), hash, req.name());

        return new RegisterResponse(id, req.email(), req.name());
    }
}
