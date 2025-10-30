package com.mpt.journal.controller;

import com.mpt.journal.entity.Profile;
import com.mpt.journal.service.ProfileService;
import com.mpt.journal.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final UserService userService;

    @GetMapping
    public String listProfiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        var response = profileService.getAllProfiles(page, size);
        model.addAttribute("profiles", response.content());
        model.addAttribute("currentPage", response.page());
        model.addAttribute("totalPages", response.totalPages());
        return "profile/list";
    }

    @GetMapping("/user/{userId}")
    public String viewOrEditProfile(@PathVariable Long userId, Model model) {
        var profile = profileService.getProfileByUserId(userId);

        if (profile != null) {
            model.addAttribute("profile", profile);
            return "profile/form";
        } else {
            // Создаём новый профиль
            Profile newProfile = new Profile();
            newProfile.setUser(userService.getUserById(userId));
            model.addAttribute("profile", newProfile);
            return "profile/form";
        }
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("profile", new Profile());
        model.addAttribute("users", userService.getAllUsers(0, 1000, null).content());
        return "profile/form";
    }

    @PostMapping
    public String createOrUpdate(@Valid @ModelAttribute Profile profile, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("users", userService.getAllUsers(0, 1000, null).content());
            return "profile/form";
        }

        if (profile.getId() != null) {
            profileService.updateProfile(profile.getId(), profile);
        } else {
            profileService.createProfile(profile);
        }

        return "redirect:/profiles";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("profile", profileService.getProfileById(id));
        model.addAttribute("users", userService.getAllUsers(0, 1000, null).content());
        return "profile/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute Profile profile,
                         BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("users", userService.getAllUsers(0, 1000, null).content());
            return "profile/form";
        }
        profileService.updateProfile(id, profile);
        return "redirect:/profiles";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        profileService.deleteProfile(id);
        return "redirect:/profiles";
    }
}