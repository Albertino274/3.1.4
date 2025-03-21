
package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.*;

@Service
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public UserServiceImp(UserRepository userRepositoryInt,
                          PasswordEncoder passwordEncoder,
                          RoleRepository roleRepository) {
        this.userRepository = userRepositoryInt;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User getUser(Long id) {
        return userRepository.findUserById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.delete(userRepository.findUserById(id));
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        User existingUser = userRepository.findUserById(user.getId());

        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());
        existingUser.setAge(user.getAge());
        if (!user.getPassword().equals(existingUser.getPassword())) {
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }
        }
        existingUser.setPhoneNumber(user.getPhoneNumber());
        existingUser.setRoles(user.getRoles());


        userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void saveUser(User user, List<Long> rolesIds) {
        Set<Role> roles = new HashSet<>();
        if (rolesIds != null) {
            for (Long roleId : rolesIds) {
                Role role = roleRepository.findById(roleId).orElse(null);
                if (role != null) {
                    roles.add(role);
                }
            }
            user.setRoles(roles);
        } else {
            Role userRole = roleRepository.findByName("USER");
            if (userRole == null) {
                userRole = new Role(1L, "USER");
                roleRepository.save(userRole);
            }
            user.setRoles(Collections.singleton(userRole));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(username);
        System.out.println("User found: " + user.getEmail() + " with roles: " + user.getRoles());

        if (user == null) {
            throw new UsernameNotFoundException("Unknown user: " + username);
        }
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user
                        .getRoles()
                        .stream()
                        .map(Role::getName)
                        .toArray(String[]::new))
                .build();
    }

    @Override
    public boolean isAdmin(User user) {
        return user.getRoles().stream()
                .anyMatch(rolesIds -> rolesIds.getName().equals("ADMIN"));
    }

    @Override
    public boolean isUser(User user) {
        return user.getRoles().stream()
                .anyMatch(rolesIds -> rolesIds.getName().equals("USER"));
    }
}
