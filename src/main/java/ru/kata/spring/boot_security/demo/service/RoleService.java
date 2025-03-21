package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Set;

public interface RoleService {
    List<Role> getAllRoles();

    Set<Role> getRolesById(List<Long> ids);

    User setRolesToUser(User user, List<Long> rolesIds);

    void saveRole(Role role);

    Role findRoleByName(String roleName);
}