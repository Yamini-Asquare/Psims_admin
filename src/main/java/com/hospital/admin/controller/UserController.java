package com.hospital.admin.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Parameter;

import com.hospital.admin.request.ForgotPasswordRequest;
import com.hospital.admin.request.UserLoginRequest;
import com.hospital.admin.request.UserRequest;
import com.hospital.admin.response.ApiResponse;
import com.hospital.admin.response.UserLoginResponse;
import com.hospital.admin.response.UserResponse;
import com.hospital.admin.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ─── Users ───────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> create(
            @Valid @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(request));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAll() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getById(
            @Parameter(required = true) @PathVariable(name = "id") UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> update(
            @Parameter(required = true) @PathVariable(name = "id") UUID id,
            @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(required = true) @PathVariable(name = "id") UUID id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }

    // ─── UserLogin ───────────────────────────────────────────────

    @PostMapping("/userLogin")
    public ResponseEntity<ApiResponse<UserLoginResponse>> createUserLogin(
            @Valid @RequestBody UserLoginRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUserLogin(request));
    }

    @GetMapping("/userLogin")
    public ResponseEntity<ApiResponse<List<UserLoginResponse>>> getAllUserLogins() {
        return ResponseEntity.ok(userService.getAllUserLogins());
    }

    @GetMapping("/userLogin/{id}")
    public ResponseEntity<ApiResponse<UserLoginResponse>> getUserLoginById(
            @Parameter(required = true) @PathVariable(name = "id") UUID id) {
        return ResponseEntity.ok(userService.getUserLoginById(id));
    }

    @PutMapping("/userLogin/{id}")
    public ResponseEntity<ApiResponse<UserLoginResponse>> updateUserLogin(
            @Parameter(required = true) @PathVariable(name = "id") UUID id,
            @Valid @RequestBody UserLoginRequest request) {
        return ResponseEntity.ok(userService.updateUserLogin(id, request));
    }

    @DeleteMapping("/userLogin/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUserLogin(
            @Parameter(required = true) @PathVariable(name = "id") UUID id) {
        return ResponseEntity.ok(userService.deleteUserLogin(id));
    }

    @PostMapping("/userLogin/forgot-password")
    public ResponseEntity<ApiResponse<UserLoginResponse>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(userService.forgotPassword(request));
    }
}
