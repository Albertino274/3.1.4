package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserService extends UserDetailsService {
    List<User> getUsers();

    User getUser(Long id);

    User getUserByEmail(String email);

    void deleteUser(Long user);

    void updateUser(User user);

    void saveUser(User user);

}