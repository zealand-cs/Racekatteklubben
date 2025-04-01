package dk.zealandcs.racekatteklubben.config;

import dk.zealandcs.racekatteklubben.domain.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;

@ControllerAdvice
public class AnnotationAdvice {
    @ModelAttribute("currentUser")
    public Optional<User> getCurrentUser(HttpSession session) {
        var user = (User)session.getAttribute("currentUser");
        return Optional.ofNullable(user);
    }
}
