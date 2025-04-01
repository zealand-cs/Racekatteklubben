package dk.zealandcs.racekatteklubben.presentation;

import dk.zealandcs.racekatteklubben.application.user.IUserService;
import dk.zealandcs.racekatteklubben.domain.Role;
import dk.zealandcs.racekatteklubben.domain.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {
    private final IUserService userService;

    UserController(IUserService userService) { this.userService = userService; }

    @GetMapping("/me")
    public String self(HttpSession session) {
        var currentUser = (User)session.getAttribute("currentUser");
        return "redirect:/users/" + currentUser.getId();
    }

    @GetMapping("/{userId}")
    public String user(@PathVariable int userId, HttpSession session, Model model) {
        var user = userService.getUser(userId);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
        }

        model.addAttribute("user", user.get());

        var currentUser = Optional.ofNullable((User)session.getAttribute("currentUser"));
        if (currentUser.isPresent() && (currentUser.get().getId() == userId || currentUser.get().getRole().isAtLeast(Role.Employee))) {
            model.addAttribute("allowEdit", true);
        } else {
            model.addAttribute("allowEdit", false);
        }

        return "users/public";
    }

    @GetMapping("/{userId}/edit")
    public String editUser(@PathVariable int userId, HttpSession session, Model model) {
        var user = userService.getUser(userId);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
        }

        var currentUser = Optional.ofNullable((User)session.getAttribute("currentUser"));
        if (currentUser.isPresent() && (currentUser.get().getId() == userId || currentUser.get().getRole().isAtLeast(Role.Employee))) {
            model.addAttribute("user", user.get());
            return "users/edit";
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "not allowed to edit user");
    }

    @PostMapping("/{userId}/edit")
    public String editRequest(@PathVariable int userId, @ModelAttribute User user, HttpSession session, Model model) {
        var currentUser = Optional.ofNullable((User)session.getAttribute("currentUser"));
        if (currentUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "not logged in");
        }

        if (userService.updateUser(currentUser.get(), user)) {
            return "redirect:/users/" + userId + "/edit";
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "not allowed to update details for user");
    }

    @PostMapping("/{userId}/edit/password")
    public String editPassword(@PathVariable int userId, @RequestParam String password, HttpSession session, Model model) {
        var user = userService.getUser(userId);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
        }

        var currentUser = Optional.ofNullable((User)session.getAttribute("currentUser"));
        if (currentUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "not logged in");
        }

        if (userService.updatePassword(currentUser.get(), user.get(), password)) {
            return "redirect:/users/" + userId + "/edit";
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "not allowed to update password for user");
    }

    @PostMapping("/{userId}/delete")
    public String deleteUser(@PathVariable int userId, HttpSession session, Model model) {
        var user = userService.getUser(userId);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
        }

        var currentUser = Optional.ofNullable((User)session.getAttribute("currentUser"));
        if (currentUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "not logged in");
        }

        if (userService.deleteUser(currentUser.get(), user.get())) {
            session.invalidate();
            model.addAttribute("user", user.get());
            return "redirect:/";
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "not allowed to delete user");
    }
}
