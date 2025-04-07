package dk.zealandcs.racekatteklubben.presentation;

import dk.zealandcs.racekatteklubben.application.user.IUserService;
import dk.zealandcs.racekatteklubben.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
public class AuthController {
    private final IUserService userService;

    AuthController(IUserService userService) { this.userService = userService; }

    @GetMapping("/login")
    public String loginPage(@ModelAttribute User user, @RequestParam(value = "redirect", required = false) String redirectUrl, Model model) {
        if (redirectUrl != null) {
            model.addAttribute("redirect", redirectUrl);
        }
        return "login/index";
    }

    @PostMapping("/login")
    public String loginRequest(@ModelAttribute User user, @RequestParam(value = "redirect", required = false) String redirectUrl, HttpSession session, Model model) {
        var loggedIn = userService.login(user.getEmail(), user.getPassword());

        if (loggedIn.isEmpty()) {
            model.addAttribute("error", "Ugyldig email eller adgangskode");
            return "login/index";
        } else {
            session.setAttribute("currentUser", loggedIn.get());
            if (redirectUrl == null) {
                return "redirect:/";
            } else {
                return "redirect:" + redirectUrl;
            }
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
