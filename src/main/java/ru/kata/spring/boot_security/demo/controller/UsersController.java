package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import ru.kata.spring.boot_security.demo.model.User;


import jakarta.validation.Valid;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;

@Controller
@Valid
public class UsersController {
    private final UserService userService;

    @Autowired
    public UsersController(UserService userServiceInt) {
        this.userService = userServiceInt;
    }

    @GetMapping(value = "/user_page")
    @PreAuthorize("hasAuthority('USER')")
    public ModelAndView getUserPage(Principal principal) {
        ModelAndView mav = new ModelAndView("/user_page");
        User user = userService.getUserByEmail(principal.getName());
        mav.addObject("user", user);
        mav.addObject("isUserRole", true);
        return mav;
    }
}