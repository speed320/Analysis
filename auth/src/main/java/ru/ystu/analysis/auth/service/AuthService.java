package ru.ystu.analysis.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ystu.analysis.auth.dto.LoginDto;
import ru.ystu.analysis.auth.dto.RegistrationDto;
import ru.ystu.analysis.auth.model.User;
import ru.ystu.analysis.auth.repository.UserRepository;
import ru.ystu.analysis.auth.security.JwtProvider;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public String register(RegistrationDto dto) {
        if(userRepository.existsByUsername(dto.username())){
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setUsername(dto.username());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setEmail(dto.email());

        userRepository.save(user);

        return jwtProvider.generateToken(user.getId(), user.getUsername());
    }

    public String login(LoginDto dto){
        User user = userRepository.findByUsername(dto.username())
                .orElseThrow(() -> new RuntimeException("User unknown"));
        if(!passwordEncoder.matches(dto.password(), user.getPassword())){
            throw new RuntimeException("Wrong password");
        }

        return jwtProvider.generateToken(user.getId(), user.getUsername());
    }
}
