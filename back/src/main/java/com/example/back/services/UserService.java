package com.example.back.services;

import com.example.back.entities.*;
import com.example.back.enums.UserRole;
import com.example.back.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public LoginResponse isValidLogin(LoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.getByEmail(loginRequest.email());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            boolean isValidPassword = bCryptPasswordEncoder.matches(loginRequest.password(), user.getPassword());
            return new LoginResponse(true, isValidPassword, user.isActive(), user.getRole());
        }
        return new LoginResponse(false, false, false, null);
    }

    public IsActiveResponse isActive(String email) {
        Optional<User> userOptional = userRepository.getByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return new IsActiveResponse(true, user.isActive());
        }
        return new IsActiveResponse(false, false);
    }

    public boolean isEmailBusy(String email) {
        Optional<User> userOptional = userRepository.getByEmail(email);
        return userOptional.isPresent();
    }

    public boolean checkRights(CheckRightsRequest request) {
        Optional<User> userOptional = userRepository.getByEmail(request.email());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return user.getEmail().equals(request.email()) &&
                    user.getRole().equals(request.role()) &&
                    user.isActive() &&
                    !user.getRole().equals(UserRole.USER);
        }
        return false;
    }

    public boolean signUpUser(SignUpRequest request) {
        User newUser = new User();
        newUser.setEmail(request.email());
        newUser.setRole(request.role());
        newUser.setPassword(bCryptPasswordEncoder.encode(request.password()));
        newUser.setActive(true);
        try {
            userRepository.save(newUser);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public ChangePasswordResponse changePassword(String email, ChangePasswordRequest request) {
        Optional<User> optionalUser = userRepository.getByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (bCryptPasswordEncoder.matches(request.oldPassword(), user.getPassword())) {
                user.setPassword(bCryptPasswordEncoder.encode(request.newPassword()));
                try {
                    userRepository.save(user);
                    return new ChangePasswordResponse(true, false, false);
                } catch (Exception e) {
                    return new ChangePasswordResponse(false, false, true);
                }
            } else {
                return new ChangePasswordResponse(false, true, false);
            }
        }
        return new ChangePasswordResponse(false, false, false); // thats mean that no such user
    }

    public DeactivateUserResponse deactivateUser(DeactivateUserRequest request) {
        if (request.whoDeactivate().equals(UserRole.USER)) {
            return new DeactivateUserResponse(false, true, false);
        }

        Optional<User> optionalUser = userRepository.getByEmail(request.email());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (request.whoDeactivate().equals(UserRole.ADMIN) && (user.equals(UserRole.ADMIN) || user.equals(UserRole.SUPER_PUPER_ADMIN))) {
                return new DeactivateUserResponse(false, true, false);
            }
            try {
                user.setActive(false);
                userRepository.save(user);
                return new DeactivateUserResponse(true, false, false);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                return new DeactivateUserResponse(false, false, false);
            }
        } else {
            return new DeactivateUserResponse(false, false, true);
        }
    }

    public List<UserForList> findAllUsersForAdmin(UserRole role) {
        List<User> users = userRepository.findAll();
        if (role.equals(UserRole.ADMIN)) {
            return users.stream().filter(u -> u.getRole().equals(UserRole.USER)).map(u -> new UserForList(u.getEmail(), u.getRole(), u.isActive())).toList();
        } else if (role.equals(UserRole.SUPER_PUPER_ADMIN)) {
            return users.stream().map(u -> new UserForList(u.getEmail(), u.getRole(), u.isActive())).toList();
        } else {
            return List.of();
        }
    }
}
