package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping(value = "/login")
    public String getLoginPage() {
        return "welcome_pages/login_page";
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

    @GetMapping(value = "/user/edit")
    @PreAuthorize("hasAuthority('USER')")
    public ModelAndView editUserForm(@RequestParam(value = "id") Long id) {
        ModelAndView mav = new ModelAndView("/user's_pages/edit_user");
        User user = userService.getUser(id);

        mav.addObject("user", user);
        return mav;
    }

    @PostMapping(value = "/user/save_edit")
    public String saveEditUser(@ModelAttribute("user") User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "/user's_pages/edit_user";
        }
        if (!user.getPassword().equals((user.getPasswordConfirm()))) {
            bindingResult.rejectValue("passwordConfirm", "error.user", "Пароли не совпадают");
            return "/user's_pages/edit_user";
        }
        userService.updateUser(user);
        return "redirect:/user_page";
    }
}