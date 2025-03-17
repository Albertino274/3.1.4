package ru.kata.spring.boot_security.demo.test_users;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

@Component
public class DatabaseCleanup {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public DatabaseCleanup(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @PreDestroy
    public void cleanup() {
        if ((userRepository.findUserByEmail("user@example.com") != null) ||
                (userRepository.findUserByEmail("admin@example.com") != null)) {
            userRepository.deleteAll();
            roleRepository.deleteAll();
        }
    }
}