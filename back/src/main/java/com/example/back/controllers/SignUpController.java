package com.example.back.controllers;

import com.example.back.entities.*;
import com.example.back.enums.UserRole;
import com.example.back.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/sign")
public class SignUpController {
    private final UserService userService;

    @Autowired
    public SignUpController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/up")
    public ResponseEntity<?> signup(@RequestBody SignUpRequest request,
                                    @SessionAttribute(name = "role", required = false) UserRole role) {
        if (userService.isEmailBusy(request.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "user with such email already exists"));
        }

        if (role == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "not able for unauthorized users"));
        }

        if (role.equals(UserRole.USER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "not allowed for users"));
        }

        if (request.role().equals(UserRole.SUPER_PUPER_ADMIN) ||
            request.role().equals(UserRole.ADMIN) && role.equals(UserRole.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "haven't such rights to create that role"));
        }

        boolean isSuccess = userService.signUpUser(request);
        if (isSuccess) {
            return ResponseEntity.ok(Map.of("message", "sign up successful"));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "internal error"));
    }

    @PostMapping("/change_password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request,
                                   @SessionAttribute(name = "email", required = false) String email) {

        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "not able for unauthorized users"));
        }

        if (request.oldPassword().isBlank() || request.newPassword().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "blank fields"));
        }

        ChangePasswordResponse response = userService.changePassword(email, request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(Map.of("message", "password change successful"));
        } else if (response.incorrectOldPassword()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "current password doesnt match"));
        } else if (response.isInternalError()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "internal error"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "no such user"));
        }
    }
}
