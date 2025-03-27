package ru.kata.spring.boot_security.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.model.UserDto;
import ru.kata.spring.boot_security.demo.service.UserConverter;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
    private final UserService userService;
    private final UserConverter userConverter;

    public UserRestController(UserService userService, UserConverter userConverter) {
        this.userService = userService;
        this.userConverter = userConverter;
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserDto> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(userConverter.convertToDto(user));
    }

    @RestController
    @RequestMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public static class AdminUserController {
        private final UserService userService;
        private final UserConverter userConverter;

        public AdminUserController(UserService userService, UserConverter userConverter) {
            this.userService = userService;
            this.userConverter = userConverter;
        }

        @GetMapping
        public ResponseEntity<List<UserDto>> getAllUsers() {
            List<User> users = userService.getUsers();
            List<UserDto> usersDto = users.stream()
                    .map(userConverter::convertToDto)
                    .toList();
            return ResponseEntity.ok(usersDto);
        }

        @GetMapping("/{id}")
        public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
            User user = userService.getUser(id);
            return ResponseEntity.ok(userConverter.convertToDto(user));
        }

        @PostMapping
        public ResponseEntity<OperationResponse> createUser(
               @RequestBody UserDto userDto) {
            User user = userConverter.convertFromDto(userDto);
            userService.saveUser(user);
            return ResponseEntity.ok(new OperationResponse("User created successfully"));
        }

        @PutMapping("/{id}")
        public ResponseEntity<OperationResponse> updateUser(
                @PathVariable Long id,
                @RequestBody UserDto userDto) {
            userDto.setId(id);
            User user = userConverter.convertFromDto(userDto);
            userService.updateUser(user);
            return ResponseEntity.ok(new OperationResponse("User updated successfully"));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<OperationResponse> deleteUser(@PathVariable Long id) {
            userService.deleteUser(id);
            return ResponseEntity.ok(new OperationResponse("User deleted successfully"));
        }
    }

    public record OperationResponse(String message) {
    }
}