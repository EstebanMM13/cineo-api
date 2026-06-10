package com.estebanmm13.auth_service.services.user;


import com.estebanmm13.auth_service.dtoModels.request.UserRequestDTO;
import com.estebanmm13.auth_service.dtoModels.response.UserResponseDTO;
import com.estebanmm13.auth_service.models.Role;
import com.estebanmm13.auth_service.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    Page<UserResponseDTO> findAllUsers(Pageable pageable);

    UserResponseDTO findUserById(Long id);

    UserResponseDTO createUser(UserRequestDTO userRequestDTO);

    UserResponseDTO updateUser(Long id, UserRequestDTO userRequestDTO);

    void deleteUser(Long id);

    UserResponseDTO findUserByUsernameIgnoreCase(String username);

    UserResponseDTO findUserByEmail(String email);

    boolean existsUserByEmail(String email);

    long countUsers();

    long countUsersByRole(Role role);

    User findUserEntityByUsername(String username);

    User getCurrentUser();

}