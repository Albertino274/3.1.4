
package ru.kata.spring.boot_security.demo.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;

@Controller
@Valid
public class RegistrationController {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public RegistrationController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping(value = "/")
    public String getWelcomePage() {
        return "/welcome_pages/welcome_page";
    }

    @GetMapping(value = "/registration")
    public String getRegistrationPage(Model model) {
        model.addAttribute("newUser", new User());
        model.addAttribute("roles", roleService.getAllRoles());
        model.addAttribute("byUser", true);
        model.addAttribute("isAdmin", false);
        model.addAttribute("isUser", true);
        return "new_user";
    }

    @PostMapping("/registration")
    public String addNewUser(@ModelAttribute("newUser")
                             @Valid User newUser,
                             @RequestParam(value = "rolesIds", required = false) List<Long> rolesIds,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "new_user";
        }
        if (!newUser.getPassword().equals(newUser.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "error.newUser", "Пароли не совпадают");
            return "new_user";
        }
        userService.saveUser(newUser, rolesIds);
        return "redirect:/login";
    }
}
