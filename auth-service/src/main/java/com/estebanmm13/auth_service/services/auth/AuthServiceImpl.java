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
        log.info("Registrando nuevo usuario con email: {} y usarname: {}",
                registerRequest.getEmail(), registerRequest.getUsername());
        // 1. Verificar si el email ya existe (para evitar el error 403)
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            log.warn("Intento de registro con email ya existente: {}", registerRequest.getEmail());
            throw new DuplicateResourceException("User", "email", registerRequest.getEmail());
        }

        // 2. Verificar si el username ya existe (opcional)
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            log.warn("Intento de registro con username ya existente: {}",registerRequest.getUsername());
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
        log.info("Usuario registrado con id: {}",savedUser.getId());
        return AuthResponse.builder().token(jwtToken).build();
    }

    @Override
    public AuthResponse authenticate(AuthenticationRequest authenticationRequest) {
        log.debug("Autenticando usario: {}",authenticationRequest.getUsername());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getUsername(),
                            authenticationRequest.getPassword()
                    ));
        } catch (BadCredentialsException e) {
            log.warn("Credenciales invalidas para usuario: {}",authenticationRequest.getUsername());
            // 3. Capturar error de credenciales y lanzar excepción personalizada
            throw new InvalidCredentialsException("User or password incorrect");
        }

        var user = userRepository.findUserByUsername(authenticationRequest.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(  // ← Excepción personalizada
                        "User", "username", authenticationRequest.getUsername()
                ));

        log.info("Usuario autenticado con exito: {}",user.getUsername());
        var jwtToken = jwtService.generateTokenWithRole(user, user.getRole().name(),user.getId());
        return AuthResponse.builder().token(jwtToken).build();
    }
}
