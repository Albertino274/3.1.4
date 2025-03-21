package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserService extends UserDetailsService {
    List<User> getUsers();

    User getUser(Long user);

    User getUserByEmail(String email);

    void deleteUser(Long id);

    void updateUser(User user);

    void saveUser(User user, List<Long> rolesIds);

    boolean isAdmin(User user);

    boolean isUser(User user);
}
