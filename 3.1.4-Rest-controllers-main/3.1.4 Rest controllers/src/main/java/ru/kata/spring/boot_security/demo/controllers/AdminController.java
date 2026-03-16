package ru.kata.spring.boot_security.demo.controllers;

import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    @Autowired
    public AdminController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @ModelAttribute("allRoles")
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @GetMapping
    public String adminPanel(Model model, @AuthenticationPrincipal User currentUser) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("showDeleteModal", false);
        model.addAttribute("userToDelete", null);
        model.addAttribute("showEditModal", false);
        model.addAttribute("editUser", null);
        return "admin";
    }

    @GetMapping("/new")
    public String newUser(Model model, @AuthenticationPrincipal User currentUser) {
        model.addAttribute("user", new User());
        model.addAttribute("currentUser", currentUser);
        return "new";
    }

    @PostMapping("/new")
    public String createUser(@ModelAttribute("user") User user,
                             @RequestParam(value = "roles", required = false) List<Long> roleIds) {
        if (roleIds != null && !roleIds.isEmpty()) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));
            user.setRoles(roles);
        } else {
            user.setRoles(new HashSet<>());
        }
        userService.save(user);
        return "redirect:/admin";
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable("id") Long id,
                           Model model,
                           @AuthenticationPrincipal User currentUser) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("showEditModal", true);
        model.addAttribute("editUser", userService.findById(id));
        // Сбрасываем флаг удаления, если был
        model.addAttribute("showDeleteModal", false);
        model.addAttribute("userToDelete", null);
        return "admin";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable("id") Long id,
                             @ModelAttribute("editUser") User user,
                             @RequestParam(value = "roles", required = false) List<Long> roleIds) {
        if (roleIds != null && !roleIds.isEmpty()) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));
            user.setRoles(roles);
        } else {
            user.setRoles(new HashSet<>());
        }
        userService.update(id, user);
        return "redirect:/admin";
    }

    @GetMapping("/delete/{id}")
    public String confirmDelete(@PathVariable("id") Long id, Model model,
                                @AuthenticationPrincipal User currentUser) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("showDeleteModal", true);
        model.addAttribute("userToDelete", userService.findById(id));
        // Сбрасываем флаг редактирования
        model.addAttribute("showEditModal", false);
        model.addAttribute("editUser", null);
        return "admin";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.delete(id);
        return "redirect:/admin";
    }
}