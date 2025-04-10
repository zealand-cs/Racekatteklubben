package dk.zealandcs.racekatteklubben.presentation;

import dk.zealandcs.racekatteklubben.application.user.IUserService;
import dk.zealandcs.racekatteklubben.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final IUserService userService;

    AuthController(IUserService userService) { this.userService = userService; }

    //Displays login page
    @GetMapping("/login")
    public String loginPage(@ModelAttribute User user, @RequestParam(value = "redirect", required = false) String redirectUrl, Model model) {
        if (redirectUrl != null) {
            model.addAttribute("redirect", redirectUrl);
            logger.debug("Redirect URL for login {}", redirectUrl);
        }
        return "login/index";
    }

    //Handles loginrequest and sets the user in session if successful login
    @PostMapping("/login")
    public String loginRequest(@ModelAttribute User user, @RequestParam(value = "redirect", required = false) String redirectUrl, HttpSession session, Model model) {
        logger.info("Login attempt for email: {}", user.getEmail());
        var loggedIn = userService.login(user.getEmail(), user.getPassword());

        if (loggedIn.isEmpty()) {
            logger.warn("Login failed for email: {}", user.getEmail());
            model.addAttribute("error", "Ugyldig email eller adgangskode");
            return "login/index";
        } else {
            logger.info("Login attempt successful for email: {}", user.getEmail());
            session.setAttribute("currentUser", loggedIn.get());
            if (redirectUrl == null) {
                logger.debug("No redirect url found for email: {}", user.getEmail());
                return "redirect:/";
            } else {
                logger.debug("Redirecting user to: {}", redirectUrl);
                return "redirect:" + redirectUrl;
            }
        }
    }

    //Displays registrationpage
    @GetMapping("/register")
    public String registerPage(@ModelAttribute User user, Model model) {
        return "register/index";
    }

    //Handles user registration
    @PostMapping("/register")
    public String registerRequest(@ModelAttribute User user, HttpSession session, Model model) {
        logger.info("Attempting registration for email: {}", user.getEmail());
        var registered = userService.register(user);

        if (registered.isEmpty()) {
            logger.warn("Registration failed for email: {}", user.getEmail());
            model.addAttribute("error", "Ugyldige detaljer.");
            return "register/index";
        }
        logger.info("Registration successful for user: {}", registered.get().getName());
        session.setAttribute("currentUser", registered.get());
        return "redirect:/register/succes";
    }

    //Confirmation page after succesful registration
    @GetMapping("/register/succes")
    public String registerSucces(HttpSession session, Model model) {
        var user = (User)session.getAttribute("currentUser");
        model.addAttribute("name", user.getName());

        return "register/succes";
    }

    //Logs out the current user by invalidating the session
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
