package com.tour.quest.controlles;

import com.tour.quest.model.Role;
import com.tour.quest.model.User;
import com.tour.quest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String userList(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "userList";
    }

    @GetMapping("{user}")
    public String editUserForm(@PathVariable User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return "userEdit";
    }

    @PostMapping
    public String userSave(
            @RequestParam("userId") User user, //get user from DB from by userId
            @RequestParam Map<String, String> form,
            @RequestParam String username
    ) {
            user.setUsername(username); //update name of user

        //  convert roles from Enum of Roles to Set<String>
        Set<String> roles = Arrays.stream(Role.values())
                .map(Role::name)
                .collect(Collectors.toSet());

        user.getRoles().clear();

        //check form (all request data) if it has roles for user
        for (String key: form.keySet()) {
            // check if in request is valid Role
            if (roles.contains(key)){
                //add particular roles to user
                user.getRoles().add(Role.valueOf(key));
            }
        }

        userRepository.save(user);
        return "redirect:/user";
    }
}
