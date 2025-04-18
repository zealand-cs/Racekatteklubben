package dk.zealandcs.racekatteklubben.presentation;

import dk.zealandcs.racekatteklubben.application.cat.ICatService;
import dk.zealandcs.racekatteklubben.application.user.IUserService;
import dk.zealandcs.racekatteklubben.domain.Role;
import dk.zealandcs.racekatteklubben.domain.User;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final IUserService userService;
    private final ICatService catService;

    UserController(IUserService userService, ICatService catService) { this.userService = userService; this.catService = catService; }

    //Displays all users
    @GetMapping
    public String allUsers(Model model) {
        logger.info("Get all users");
        model.addAttribute("users", userService.allUsers());
        return "users/all";
    }

    //Redirects logged in user to their own profile
    @GetMapping("/me")
    public String self(HttpSession session) {
        var currentUser = (User)session.getAttribute("currentUser");
        logger.info("Redirecting to user {}", currentUser.getId());
        return "redirect:/users/" + currentUser.getId();
    }

    //Views a users public profile
    @GetMapping("/{userId}")
    public String user(@PathVariable int userId, HttpSession session, Model model) {
        logger.info("Fetching user {}", userId);
        var user = userService.getUser(userId);
        if (user.isEmpty()) {
            logger.error("User {} not found", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
        }

        model.addAttribute("user", user.get());

        var cats = catService.getCatsByOwner(userId);
        model.addAttribute("cats", cats);

        var currentUser = Optional.ofNullable((User)session.getAttribute("currentUser"));
        if (currentUser.isPresent() && (currentUser.get().getId() == userId || currentUser.get().getRole().isAtLeast(Role.Employee))) {
            model.addAttribute("allowEdit", true);
        } else {
            model.addAttribute("allowEdit", false);
        }

        return "users/public";
    }

    //Shows edit form if permission
    @GetMapping("/{userId}/edit")
    public String editUser(@PathVariable int userId, HttpSession session, Model model) {
        logger.info("Attempting to edit user {}", userId);
        var user = userService.getUser(userId);
        if (user.isEmpty()) {
            logger.error("User {} not found", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
        }

        var currentUser = Optional.ofNullable((User)session.getAttribute("currentUser"));
        if (currentUser.isPresent() && (currentUser.get().getId() == userId || currentUser.get().getRole().isAtLeast(Role.Employee))) {
            model.addAttribute("user", user.get());
            return "users/edit";
        }
        logger.error("User {} not allowed to edit", userId);
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "not allowed to edit user");
    }

    //Handles user update (if allowed)
    @PostMapping("/{userId}/edit")
    public String editRequest(@PathVariable int userId, @ModelAttribute User user, HttpSession session, Model model) {
        var currentUser = Optional.ofNullable((User)session.getAttribute("currentUser"));
        if (currentUser.isEmpty()) {
            logger.warn("Edit request denied - no logged in user");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "not logged in");
        }

        if (userService.updateUser(currentUser.get(), user)) {
            logger.info("User {} updated user {}", currentUser.get().getId(), user.getId());
            return "redirect:/users/" + userId + "/edit";
        }
        logger.warn("User {} not allowed to update user {}", currentUser.get().getId(), user.getId());
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "not allowed to update details for user");
    }


    //Handles password change (if allowed)
    @PostMapping("/{userId}/edit/password")
    public String editPassword(@PathVariable int userId, @RequestParam String password, HttpSession session, Model model) {
        var user = userService.getUser(userId);
        if (user.isEmpty()) {
            logger.warn("Password update failed - user {} not found", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
        }

        var currentUser = Optional.ofNullable((User)session.getAttribute("currentUser"));
        if (currentUser.isEmpty()) {
            logger.warn("Password update failed - no logged in user");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "not logged in");
        }

        if (userService.updatePassword(currentUser.get(), user.get(), password)) {
            logger.info("User {} updated password {}", currentUser.get().getId(), user.get());
            return "redirect:/users/" + userId + "/edit";
        }
        logger.warn("User {} not allowed to update password for user {}", currentUser.get().getId(), user.get());
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "not allowed to update password for user");
    }

    //Handles role change
    @PostMapping("/{userId}/edit/role")
    public String editRole(@PathVariable int userId, @RequestParam Role role, HttpSession session, Model model) {
        var user = userService.getUser(userId);
        if (user.isEmpty()) {
            logger.warn("Role update failed - user {} not found", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
        }

        var currentUser = Optional.ofNullable((User)session.getAttribute("currentUser"));
        if (currentUser.isEmpty()) {
            logger.warn("Role update failed - no logged in user");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "not logged in");
        }

        if (userService.updateRole(currentUser.get(), user.get(), role)) {
            logger.info("User {} updated role {}", currentUser.get().getId(), user.get());
            return "redirect:/users/" + userId + "/edit";
        }
        logger.warn("User {} not allowed to update role for user {}", currentUser.get().getId(), user.get());
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "not allowed to update password for user");
    }

    //Delete user (if allowed)
    @PostMapping("/{userId}/delete")
    public String deleteUser(@PathVariable int userId, HttpSession session, Model model) {
        var user = userService.getUser(userId);
        if (user.isEmpty()) {
            logger.warn("User delete failed - user {} not found", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
        }

        var currentUser = Optional.ofNullable((User)session.getAttribute("currentUser"));
        if (currentUser.isEmpty()) {
            logger.warn("User delete failed - no logged in user");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "not logged in");
        }

        if (userService.deleteUser(currentUser.get(), user.get())) {
            logger.info("User {} deleted user {}", currentUser.get().getId(), user.get());
            session.invalidate();
            model.addAttribute("user", user.get());
            return "redirect:/";
        }

        logger.warn("User {} not allowed to delete user {}", currentUser.get().getId(), user.get());
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "not allowed to delete user");
    }
}
