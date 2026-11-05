package com.example.back.controllers;

import com.example.back.entities.DeactivateRequest;
import com.example.back.entities.DeactivateUserRequest;
import com.example.back.entities.DeactivateUserResponse;
import com.example.back.enums.UserRole;
import com.example.back.services.SessionService;
import com.example.back.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserService userService;
    private final SessionService sessionService;

    public AdminController(UserService userService, SessionService sessionService) {
        this.userService = userService;
        this.sessionService = sessionService;
    }

    @PostMapping("/deactivate")
    public ResponseEntity<?> deactivateUserByEmail(@RequestBody DeactivateRequest request,
                                                   @SessionAttribute(name = "role", required = false) UserRole role) {
        if (role == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "not able for unauthorized users"));
        }

        if (role.equals(UserRole.USER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "not allowed for users"));
        }

        DeactivateUserResponse response = userService.deactivateUser(new DeactivateUserRequest(request.email(), role));

        if (response.isSuccess()) {
            sessionService.invalidateAllSessions(request.email());
            return ResponseEntity.ok(Map.of("message", "ok"));
        } else if (response.isForbidden()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "not such rights"));
        } else if (response.noSuchUser()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "not such user"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "unknown error"));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@SessionAttribute(name = "role", required = false) UserRole role) {
        if (role == null || !role.equals(UserRole.SUPER_PUPER_ADMIN) && !role.equals(UserRole.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "no such rights"));
        }

        return ResponseEntity.ok(userService.findAllUsersForAdmin(role));
    }
}
