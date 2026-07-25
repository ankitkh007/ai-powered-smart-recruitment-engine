package com.recruitment.engine.controller;

import com.recruitment.engine.dto.request.RegisterUserRequestDto;
import com.recruitment.engine.dto.response.UserResponseDto;
import com.recruitment.engine.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/recruiters")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createRecruiter(@Valid @RequestBody RegisterUserRequestDto request) {
        UserResponseDto created = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllRecruiters() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<UserResponseDto> deactivateRecruiter(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deactivateUser(id));
    }
}