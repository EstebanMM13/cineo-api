package com.estebanmm13.auth_service.mapper;


import com.estebanmm13.auth_service.dtoModels.request.UserRequestDTO;
import com.estebanmm13.auth_service.dtoModels.response.UserResponseDTO;
import com.estebanmm13.auth_service.models.Role;
import com.estebanmm13.auth_service.models.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponseDTO toResponseDTO(User user) {
        if (user == null) return null;
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole() != null ? user.getRole().name() : null
        );
    }

    public User toEntity(UserRequestDTO dto) {
        if (dto == null) return null;
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword()); // Se encriptará después en el servicio
        if (dto.getRole() != null) {
            user.setRole(Role.valueOf(dto.getRole()));
        }
        return user;
    }

    public void updateEntity(User existing, UserRequestDTO dto) {
        if (dto.getUsername() != null) existing.setUsername(dto.getUsername());
        if (dto.getEmail() != null) existing.setEmail(dto.getEmail());
        if (dto.getPassword() != null) existing.setPassword(dto.getPassword()); // Ojo: encriptar luego
        if (dto.getRole() != null) existing.setRole(Role.valueOf(dto.getRole()));
    }
}
