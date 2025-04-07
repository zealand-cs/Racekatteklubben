package dk.zealandcs.racekatteklubben.presentation;

import dk.zealandcs.racekatteklubben.application.cat.ICatService;
import dk.zealandcs.racekatteklubben.domain.Cat;
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
@RequestMapping("/cats")
public class CatController {
    private final ICatService catService;

    CatController(ICatService catService) { this.catService = catService; }

    @GetMapping
    public String findCats(Model model) {
        model.addAttribute("cats", catService.allCats());
        return "cats/all";
    }

    @PostMapping
    public String createCat(@ModelAttribute Cat cat, HttpSession session, Model model) {
        var currentUser = Optional.ofNullable((User)session.getAttribute("currentUser"));
        if (currentUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "not logged in");
        }

        cat.setOwnerId(currentUser.get().getId());

        var newCat = catService.createCat(cat);
        if (newCat.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "error while creating new cat");
        }

        return "redirect:/cats/" + newCat.get().getId();
    }

    @GetMapping("/create")
    public String createCatPage(HttpSession session, Model model) {
        var currentUser = Optional.ofNullable((User)session.getAttribute("currentUser"));
        if (currentUser.isEmpty()) {
            return "redirect:/login?redirect=/cats/create";
        }

        return "/cats/create";
    }

    @GetMapping("/{catId}")
    public String cat(@PathVariable int catId, HttpSession session, Model model) {
        var cat = catService.getCat(catId);
        if (cat.isEmpty()) {
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

    @GetMapping("/{catId}/edit")
    public String editCatPage(@PathVariable int catId, HttpSession session, Model model) {
        var cat = catService.getCat(catId);
        if (cat.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "cat not found");
        }

        var currentUser = Optional.ofNullable((User)session.getAttribute("currentUser"));
        if (currentUser.isPresent() && (currentUser.get().getId() == cat.get().getOwnerId() || currentUser.get().getRole().isAtLeast(Role.Admin))) {
            model.addAttribute("cat", cat.get());
            return "cats/edit";
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "no access to edit cat");
        }
    }

    @PostMapping("/{catId}/edit")
    public String updateCat(@PathVariable int catId, @ModelAttribute Cat cat, HttpSession session, Model model) {
        var currentUser = Optional.ofNullable((User)session.getAttribute("currentUser"));
        if (currentUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "not logged in");
        }

        var catCheck = catService.getCat(catId);
        if (catCheck.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "cat not found");
        }

        cat.setId(catId);
        cat.setOwnerId(catCheck.get().getOwnerId());

        if (catService.editCat(currentUser.get(), cat)) {
            model.addAttribute("cat", cat);
            return "redirect:/cats/" + catId;
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "no access to edit cat");
    }

    @PostMapping("/{catId}/delete")
    public String deleteCat(@PathVariable int catId, HttpSession session, Model model) {
        var currentUser = Optional.ofNullable((User)session.getAttribute("currentUser"));
        if (currentUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "not logged in");
        }

        var cat = catService.getCat(catId);
        if (cat.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "cat not found");
        }

        if (catService.deleteCat(currentUser.get(), cat.get())) {
            return "redirect:/cats";
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "no access to delete cat");
    }
}
