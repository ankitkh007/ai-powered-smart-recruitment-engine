package com.recruitment.engine.service;

import com.recruitment.engine.dto.request.RegisterUserRequestDto;
import com.recruitment.engine.dto.response.UserResponseDto;
import com.recruitment.engine.entity.Role;
import com.recruitment.engine.entity.User;
import com.recruitment.engine.exception.DuplicateResourceException;
import com.recruitment.engine.exception.ResourceNotFoundException;
import com.recruitment.engine.repository.RoleRepository;
import com.recruitment.engine.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDto createUser(RegisterUserRequestDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("A user with this email already exists");
        }

        Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid role: " + request.getRole()));

        User user = new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                role);

        User saved = userRepository.save(user);
        return toResponseDto(saved);
    }

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponseDto)
                .toList();
    }

    public UserResponseDto deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setActive(false);
        User saved = userRepository.save(user);
        return toResponseDto(saved);
    }

    private UserResponseDto toResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().getName().name(),
                user.isActive(),
                user.getCreatedAt());
    }
}