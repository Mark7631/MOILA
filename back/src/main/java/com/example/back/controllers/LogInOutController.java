package com.example.back.controllers;

import com.example.back.entities.LoginRequest;
import com.example.back.entities.LoginResponse;
import com.example.back.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/log")
public class LogInOutController {
    private final UserService userService;

    @Autowired
    public LogInOutController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/in")
    public ResponseEntity<?> login(@RequestBody LoginRequest request,
                                         HttpServletRequest httpServletRequest) {
        LoginResponse res = userService.isValidLogin(request);
        if (!res.isUserExist()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "invalid email"));
        } else if (!res.isValidPassword()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "invalid newPassword"));
        } else if (!res.isActive()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "user deactivated"));
        }

        HttpSession session = httpServletRequest.getSession(true);
        session.setAttribute("email", request.email());
        session.setAttribute("role", res.userRole());

        session.setAttribute(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, request.email());
        return ResponseEntity.ok(Map.of("message", "login successful"));
    }

    @PostMapping("/out")
    public ResponseEntity<?> logout(HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(false);

        if (session == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "wasn't authorized"));
        }

        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "logout successful"));
    }
}
