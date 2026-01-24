package com.monst.service;

import com.monst.dto.request.RegisterRequest;
import com.monst.dto.response.RegisterResponse;
import com.monst.exception.EmailAlreadyUsedException;
import com.monst.repository.UserRepository;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public RegisterResponse register(RegisterRequest req) {
        // 事前チェック（ユーザー体験向上）
        if (userRepository.findByEmail(req.email()).isPresent()) {
            throw new EmailAlreadyUsedException();
        }

        String hash = passwordEncoder.encode(req.password());

        try {
            long id = userRepository.insert(req.email(), hash, req.name());
            return new RegisterResponse(id, req.email(), req.name());
        } catch (DuplicateKeyException e) {
            // 競合（同時登録など）でDBのUNIQUE制約に引っかかった場合
            throw new EmailAlreadyUsedException();
        }
    }
}
