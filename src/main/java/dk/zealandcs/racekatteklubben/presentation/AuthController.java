package dk.zealandcs.racekatteklubben.presentation;

import dk.zealandcs.racekatteklubben.application.user.IUserService;
import dk.zealandcs.racekatteklubben.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
public class AuthController {
    private final IUserService userService;

    AuthController(IUserService userService) { this.userService = userService; }

    @GetMapping("/login")
    public String loginPage(@ModelAttribute User user, HttpSession session, Model model) {
        return "login/index";
    }

    @PostMapping("/login")
    public String loginRequest(@ModelAttribute User user, HttpSession session, Model model) {
        var loggedIn = userService.login(user.getEmail(), user.getPassword());

        if (loggedIn.isEmpty()) {
            model.addAttribute("error", "Ugyldig email eller adgangskode");
        } else {
            session.setAttribute("currentUser", loggedIn);
        }

        return "login";
    }

    @GetMapping("/register")
    public String registerPage(@ModelAttribute User user, Model model) {
        return "register/index";
    }

    @PostMapping("/register")
    public String registerRequest(@ModelAttribute User user, Model model) {
        return "register/succes";
    }
}
