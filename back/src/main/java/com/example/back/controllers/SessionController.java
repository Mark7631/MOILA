package com.example.back.controllers;

import com.example.back.entities.DeactivateRequest;
import com.example.back.entities.IsActiveResponse;
import com.example.back.enums.UserRole;
import com.example.back.services.SessionService;
import com.example.back.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/session")
public class SessionController {
    private final UserService userService;
    private final SessionService sessionService;

    @Autowired
    public SessionController(UserService userService, SessionService sessionService) {
        this.userService = userService;
        this.sessionService = sessionService;
    }

    @GetMapping("/info")
    public ResponseEntity<?> info(HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(false);

        if (session == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "not authenticated"));
        }
        var emailAttribute = session.getAttribute("email");
        if (!(emailAttribute instanceof String email)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "not authenticated"));
        }
        UserRole role = (UserRole) session.getAttribute("role");
        IsActiveResponse res = userService.isActive(email);
        if (!res.isUserExist()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "authenticated, but haven't such user in db"));
        }

        return ResponseEntity.ok(Map.of(
            "email", email,
            "role", role,
            "isActive", res.isActive()
        ));
    }

    @PostMapping("/deactivate")
    public ResponseEntity<?> deactivate(@RequestBody DeactivateRequest request) {
        int deletedSessions = sessionService.invalidateAllSessions(request.email());
        return ResponseEntity.ok(Map.of("deletedSessions", deletedSessions));
    }
}