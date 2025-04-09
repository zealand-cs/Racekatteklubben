package dk.zealandcs.racekatteklubben.presentation;

import dk.zealandcs.racekatteklubben.application.cat.ICatService;
import dk.zealandcs.racekatteklubben.domain.Cat;
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
@RequestMapping("/cats")
public class CatController {
    private static final Logger logger = LoggerFactory.getLogger(CatController.class);
    private final ICatService catService;

    CatController(ICatService catService) { this.catService = catService; }

    //Show all cats
    @GetMapping
    public String findCats(Model model) {
        logger.info("Fetch cats");
        model.addAttribute("cats", catService.allCats());
        return "cats/all";
    }
    //Create new cat (POST)
    @PostMapping
    public String createCat(@ModelAttribute Cat cat, HttpSession session, Model model) {
        var currentUser = Optional.ofNullable((User)session.getAttribute("currentUser"));
        if (currentUser.isEmpty()) {
            logger.warn("Unauthenticated user");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "not logged in");
        }

        cat.setOwnerId(currentUser.get().getId());

        var newCat = catService.createCat(cat);
        if (newCat.isEmpty()) {
            logger.error("Failed to create new cat");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "error while creating new cat");
        }
        logger.info("New cat created with ID" + newCat.get().getId());
        return "redirect:/cats/" + newCat.get().getId();
    }

    //Show page for creating new cat
    @GetMapping("/create")
    public String createCatPage(HttpSession session, Model model) {
        var currentUser = Optional.ofNullable((User)session.getAttribute("currentUser"));
        if (currentUser.isEmpty()) {
            logger.info("Redirecting unauthenticated user to login before creating cat");
            return "redirect:/login?redirect=/cats/create";
        }

        return "/cats/create";
    }

    //Show one cats info
    @GetMapping("/{catId}")
    public String cat(@PathVariable int catId, HttpSession session, Model model) {
        var cat = catService.getCat(catId);
        if (cat.isEmpty()) {
            logger.warn("Cat with ID not found", catId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "cat not found");
        }

        model.addAttribute("cat", cat.get());

        var currentUser = Optional.ofNullable((User)session.getAttribute("currentUser"));
        if (currentUser.isPresent() && (currentUser.get().getId() == cat.get().getOwnerId() || currentUser.get().getRole().isAtLeast(Role.Admin))) {
            model.addAttribute("allowEdit", true);
        } else {
            model.addAttribute("allowEdit", false);
        }

        return "cats/public";
    }
    // Show page for edit cat
    @GetMapping("/{catId}/edit")
    public String editCatPage(@PathVariable int catId, HttpSession session, Model model) {
        var cat = catService.getCat(catId);
        if (cat.isEmpty()) {
            logger.warn("Cat with ID not found", catId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "cat not found");
        }

        var currentUser = Optional.ofNullable((User)session.getAttribute("currentUser"));
        if (currentUser.isPresent() && (currentUser.get().getId() == cat.get().getOwnerId() || currentUser.get().getRole().isAtLeast(Role.Admin))) {
            model.addAttribute("cat", cat.get());
            logger.info("Editing cat ID" + catId);
            return "cats/edit";
        } else {
            logger.warn("User tried to edit cat ID without permission");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "no access to edit cat");
        }
    }
    //Save edits for a cat
    @PostMapping("/{catId}/edit")
    public String updateCat(@PathVariable int catId, @ModelAttribute Cat cat, HttpSession session, Model model) {
        var currentUser = Optional.ofNullable((User)session.getAttribute("currentUser"));
        if (currentUser.isEmpty()) {
            logger.warn("Unauthorized access to update cat ID");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "not logged in");
        }

        var catCheck = catService.getCat(catId);
        if (catCheck.isEmpty()) {
            logger.warn("Cat with ID not found", catId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "cat not found");
        }

        cat.setId(catId);
        cat.setOwnerId(catCheck.get().getOwnerId());

        if (catService.editCat(currentUser.get(), cat)) {
            model.addAttribute("cat", cat);
            return "redirect:/cats/" + catId;
        }
        logger.warn("User not authorized to edit cat ID");
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "no access to edit cat");
    }

    //Delete cat
    @PostMapping("/{catId}/delete")
    public String deleteCat(@PathVariable int catId, HttpSession session, Model model) {
        var currentUser = Optional.ofNullable((User)session.getAttribute("currentUser"));
        if (currentUser.isEmpty()) {
            logger.warn("Unauthorized access to delete cat ID");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "not logged in");
        }

        var cat = catService.getCat(catId);
        if (cat.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "cat not found");
        }

        if (catService.deleteCat(currentUser.get(), cat.get())) {
            logger.info("Cat ID deleted by user ID", catId, currentUser.get().getId());
            return "redirect:/cats";
        }
        logger.warn("User not authorized to delete cat ID");
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "no access to delete cat");
    }
}
