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
    public String loginPage(@ModelAttribute User user, Model model) {
        return "login/index";
    }

    @PostMapping("/login")
    public String loginRequest(@ModelAttribute User user, HttpSession session, Model model) {
        var loggedIn = userService.login(user.getEmail(), user.getPassword());

        if (loggedIn.isEmpty()) {
            model.addAttribute("error", "Ugyldig email eller adgangskode");
            return "login/index";
        } else {
            session.setAttribute("currentUser", loggedIn.get());
            return "redirect:/";
        }
    }

    @GetMapping("/register")
    public String registerPage(@ModelAttribute User user, Model model) {
        return "register/index";
    }

    @PostMapping("/register")
    public String registerRequest(@ModelAttribute User user, HttpSession session, Model model) {
        var registered = userService.register(user);

        if (registered.isEmpty()) {
            model.addAttribute("error", "Ugyldige detaljer.");
            return "register/index";
        }
        session.setAttribute("currentUser", registered.get());

        return "redirect:/register/succes";
    }

    @GetMapping("/register/succes")
    public String registerSucces(HttpSession session, Model model) {
        var user = (User)session.getAttribute("currentUser");
        model.addAttribute("name", user.getName());

        return "register/succes";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
