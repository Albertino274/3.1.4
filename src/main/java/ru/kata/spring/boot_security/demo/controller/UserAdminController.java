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
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.util.UserConverter;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserAdminController {
    private final UserService userService;
    private final UserConverter userConverter;

    public UserAdminController(UserService userService, UserConverter userConverter) {
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

    @GetMapping("/current")
    public ResponseEntity<UserDto> getCurrentAdminUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(userConverter.convertToDto(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        User user = userService.getUser(id);
        return ResponseEntity.ok(userConverter.convertToDto(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateUser(
            @PathVariable Long id,
            @RequestBody UserDto userDto) {
        userDto.setId(id);
        User user = userConverter.convertFromDto(userDto);
        userService.updateUser(user);
        return ResponseEntity.ok(Map.of("message", "User updated successfully"));
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createUser(@RequestBody UserDto userDto) {
        User user = userConverter.convertFromDto(userDto);
        userService.saveUser(user);
        return ResponseEntity.ok(Map.of("message", "User created successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }
}