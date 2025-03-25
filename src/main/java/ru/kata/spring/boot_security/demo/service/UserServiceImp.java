package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.model.UserDTO;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImp implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final RoleService roleService;

    @Autowired
    public UserServiceImp(UserRepository userRepositoryInt,
                          PasswordEncoder passwordEncoder,
                          RoleRepository roleRepository, RoleService roleService) {
        this.userRepository = userRepositoryInt;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.roleService = roleService;
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
    public void deleteUser(User user) {
        userRepository.delete(userRepository.findUserByEmail(user.getEmail()));
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        User existingUser = userRepository.findUserById(user.getId());

        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());
        existingUser.setAge(user.getAge());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            if (!user.getPassword().equals(existingUser.getPassword())) {
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }
        }
        existingUser.setPhoneNumber(user.getPhoneNumber());
        existingUser.setRoles(user.getRoles());


        userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void saveUser(User user) {
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

    @Override
    public UserDTO setDataToUser(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmail(user.getEmail());
        userDTO.setAge(user.getAge());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setRoles(user
                .getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toList()));
        return userDTO;
    }

    @Override
    public User convertDataFromUserDTO(UserDTO userDTO) {
        User user = new User();
        if (userDTO.getId() != null) {
            user.setId(userDTO.getId());
        }
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setAge(userDTO.getAge());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setEmail(userDTO.getEmail());
        System.out.println(userDTO.getPassword());

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(userDTO.getPassword());
        } else {
            user.setPassword(userRepository.findUserByEmail(userDTO.getEmail()).getPassword());
        }

        user.setRoles(userDTO.getRoles()
                .stream().map(roleService::findRoleByName)
                .collect(Collectors.toSet()));
        return user;
    }
}