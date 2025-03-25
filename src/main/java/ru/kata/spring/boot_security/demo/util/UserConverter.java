package ru.kata.spring.boot_security.demo.util;

import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.model.UserDto;
import ru.kata.spring.boot_security.demo.service.RoleService;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserConverter {
    private final RoleService roleService;

    public UserConverter(RoleService roleService) {
        this.roleService = roleService;
    }

    public UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setAge(user.getAge());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setPassword("");
        dto.setRoles(user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet()));
        return dto;
    }

    public User convertFromDto(UserDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setAge(dto.getAge());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setPassword(dto.getPassword());

        Set<Role> roles = dto.getRoles().stream()
                .map(roleService::findRoleByName)
                .collect(Collectors.toSet());
        user.setRoles(roles);

        return user;
    }
}
