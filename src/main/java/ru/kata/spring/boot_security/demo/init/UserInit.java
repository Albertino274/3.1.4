package ru.kata.spring.boot_security.demo.init;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RegistrationService;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Set;

@Component
public class UserInit {
    private final RegistrationService registrationService;
    private final UserService userService;
    private final RoleService roleService;

    public UserInit(RegistrationService registrationService, UserService userService, RoleService roleService) {
        this.registrationService = registrationService;
        this.userService = userService;
        this.roleService = roleService;
    }

    @PostConstruct
    public void userRegistration(){
        if(roleService.findByName("ROLE_USER").isEmpty()){
            roleService.save(new Role("ROLE_USER"));
        }

        if (roleService.findByName("ROLE_ADMIN").isEmpty()) {
            roleService.save(new Role("ROLE_ADMIN"));
        }

        if (userService.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setLast_name("admin");
            admin.setEmail("admin@admin.com");
            admin.setPassword("root");
            Role roleBase = roleService.findByName("ROLE_ADMIN").orElseThrow(() ->
                    new RuntimeException("Роль не найдена"));
            admin.setUserRole(Set.of(roleBase));
            registrationService.saveUser(admin);
        }

        if (userService.findByUsername("user").isEmpty()) {
            User user = new User();
            user.setUsername("user");
            user.setLast_name("user");
            user.setEmail("user@user.com");
            user.setPassword("root");
            Role roleBase = roleService.findByName("ROLE_USER").orElseThrow(() ->
                    new RuntimeException("Роль не найдена"));
            user.setUserRole(Set.of(roleBase));
            registrationService.saveUser(user);
        }
    }

    @PreDestroy
    public void deleteTestUser(){
        User admin = userService.findByUsername("admin").orElseThrow();
        userService.deleteUserById(admin.getId());

        User user = userService.findByUsername("user").orElseThrow();
        userService.deleteUserById(user.getId());
    }
}
