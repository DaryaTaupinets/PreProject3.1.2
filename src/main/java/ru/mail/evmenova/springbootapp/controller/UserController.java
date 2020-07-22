package ru.mail.evmenova.springbootapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.mail.evmenova.springbootapp.model.Role;
import ru.mail.evmenova.springbootapp.model.User;
import ru.mail.evmenova.springbootapp.repository.RoleRepository;
import ru.mail.evmenova.springbootapp.repository.UserRepository;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@Controller
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/admin")
    public String main(User user, Model model) {
        User currentUser = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("currentUser", currentUser);
        return "main";
    }

    @GetMapping("/user")
    public String showUserPage(User user, Model model){
        User currentUser = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        model.addAttribute("currentUser", currentUser);
        return "user-page";
    }

    @PostMapping("filter")
    public String filter(@RequestParam String filter,
                         Model model) {
        User currentUser = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        Iterable<User> users;
        if (filter != null && !filter.isEmpty()) {
            users = userRepository.findByFirstName(filter);
        } else {
            users = userRepository.findAll();
        }
        model.addAttribute("users", users);
        model.addAttribute("currentUser", currentUser);
        return "main";
    }

    @PostMapping("/admin/create")
    public String addUser(@Valid User user,
                          @RequestParam(value = "role") String roleName) {
        Set<Role> roles = new HashSet<>();
        String [] split = roleName.split(",");
        for (String role : split) {
            roles.add(roleRepository.findByRoleName(role));
        }
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/admin";
    }

    @GetMapping("/admin/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id,
                                 Model model) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id: " + id));
        model.addAttribute("user", user);
        return "redirect:/admin";
    }

    @PostMapping("/admin/update/{id}")
    public String updateUser(@PathVariable("id") Integer id,
                             @Valid User user,
                             BindingResult result,
                             @RequestParam(value = "role") String roleName) {
        if (result.hasErrors()) {
            user.setId(id);
            return "redirect:/admin";
        }
        Set<Role> roles = new HashSet<>();
        String[] split = roleName.split(",");
        for (String role : split) {
            roles.add(roleRepository.findByRoleName(role));
        }
        user.setRoles(roles);
        userRepository.save(user);
        return "redirect:/admin";
    }

    @GetMapping("admin/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id,
                             Model model) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id: " + id));
        userRepository.delete(user);
        model.addAttribute("users", userRepository.findAll());
        return "redirect:/admin";
    }
}
