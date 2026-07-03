package com.estebanmm13.auth_service.services.auth;


import com.estebanmm13.auth_service.config.JwtService;
import com.estebanmm13.auth_service.dtoModels.request.AuthenticationRequest;
import com.estebanmm13.auth_service.dtoModels.request.RegisterRequest;
import com.estebanmm13.auth_service.dtoModels.response.AuthResponse;
import com.estebanmm13.auth_service.error.DuplicateResourceException;
import com.estebanmm13.auth_service.error.InvalidCredentialsException;
import com.estebanmm13.auth_service.error.ResourceNotFoundException;
import com.estebanmm13.auth_service.models.Role;
import com.estebanmm13.auth_service.models.User;
import com.estebanmm13.auth_service.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest registerRequest) {
        log.info("Registering new user with email: {} and username: {}",
                registerRequest.getEmail(), registerRequest.getUsername());
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            log.warn("Registration attempt with existing email: {}", registerRequest.getEmail());
            throw new DuplicateResourceException("User", "email", registerRequest.getEmail());
        }

        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            log.warn("Registration attempt with existing username: {}", registerRequest.getUsername());
            throw new DuplicateResourceException("User", "username", registerRequest.getUsername());
        }

        var user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateTokenWithRole(savedUser, savedUser.getRole().name(), savedUser.getId());
        log.info("User registered with id: {}", savedUser.getId());
        return AuthResponse.builder().token(jwtToken).build();
    }

    @Override
    public AuthResponse authenticate(AuthenticationRequest authenticationRequest) {
        log.debug("Authenticating user: {}", authenticationRequest.getUsername());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getUsername(),
                            authenticationRequest.getPassword()
                    ));
        } catch (BadCredentialsException e) {
            log.warn("Invalid credentials for user: {}", authenticationRequest.getUsername());
            throw new InvalidCredentialsException("User or password incorrect");
        }

        var user = userRepository.findUserByUsername(authenticationRequest.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User", "username", authenticationRequest.getUsername()
                ));

        log.info("User authenticated successfully: {}", user.getUsername());
        var jwtToken = jwtService.generateTokenWithRole(user, user.getRole().name(),user.getId());
        return AuthResponse.builder().token(jwtToken).build();
    }
}
