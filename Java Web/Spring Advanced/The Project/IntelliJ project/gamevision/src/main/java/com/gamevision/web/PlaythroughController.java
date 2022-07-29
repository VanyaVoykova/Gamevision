package com.gamevision.web;

import com.gamevision.model.binding.GameAddBindingModel;
import com.gamevision.model.binding.PlaythroughAddBindingModel;
import com.gamevision.model.user.GamevisionUserDetails;
import com.gamevision.model.view.PlaythroughViewModel;
import com.gamevision.repository.PlaythroughRepository;
import com.gamevision.service.GameService;
import com.gamevision.service.PlaythroughService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/games/{gameId}/playthroughs") //todo: check url - use it in the show/hide script
public class PlaythroughController {
    private final PlaythroughRepository playthroughRepository;
    private final PlaythroughService playthroughService;
    private final GameService gameService;

    public PlaythroughController(PlaythroughRepository playthroughRepository, PlaythroughService playthroughService, GameService gameService) {
        this.playthroughRepository = playthroughRepository;
        this.playthroughService = playthroughService;
        this.gameService = gameService;
    }

    @GetMapping("/all")
    public String getAllPlaythroughsForGame(@PathVariable("gameId") Long gameId, Model model) {
        try {
            String gameTitle = gameService.getGameTitleById(gameId);
            List<PlaythroughViewModel> playthroughs = playthroughService.getAllPlaythroughsForGame(gameId);
            if (playthroughs == null) {
                model.addAttribute("noPlaythroughsFound", "No playthroughs found.");
                return "/errors/playthroughs-not-found-error";
            }

            if (gameTitle == null) {
                model.addAttribute("gameNotFound", "Game not found.");
                return "/errors/game-not-found-error";
            }

            model.addAttribute("gameTitle", gameTitle);
            model.addAttribute("playthroughs", playthroughs);
            model.addAttribute("gameId", gameId); //for the th:action in template

        } catch (Exception e) {
            model.addAttribute("exceptionMessage", e.getMessage()); //or with model???
            System.out.println(e.getMessage());
            return "/errors/game-not-found-error";
        }

        return "playthroughs-all"; //with a separate playthroughs template & page
      // return "redirect:/games/{gameId}"; //trying to show them on the game's page with show/hide Playthroughs/Comments

    }

    @GetMapping("/add")
    public String addPlaythrough(@PathVariable("gameId") Long gameId, Model model) {
        if (!model.containsAttribute("playthroughAddBindingModel")) {
            model.addAttribute("playthroughAddBindingModel", new PlaythroughAddBindingModel());
        }

        return "playthrough-add";
    }


    @PostMapping("/add")
    public String addPlaythroughSubmit(@PathVariable("gameId") Long gameId, PlaythroughAddBindingModel playthroughAddBindingModel,
                                       BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        try {
            String gameTitle = gameService.getGameTitleById(gameId); //throws GameNotFoundException
            model.addAttribute("gameTitle", gameTitle);
        } catch (Exception e) {
            model.addAttribute("gameNotFound", "Game not found.");
            System.out.println(e.getMessage());
            return "/errors/game-not-found-error"; //checking only for existing game here, so it should be this error
            //model.addAttribute("exceptionMessage", e.getMessage()); //or with model???
        }

//Game found, game title added to model, forge ahead

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("playthroughAddBindingModel", playthroughAddBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.playthroughAddBindingModel", bindingResult);
            return "redirect:/add/{gameId}";
        }

        //  playthroughAddBindingModel.setUsername(userDetails.getUsername());
        playthroughService.addPlaythrough(gameId, playthroughAddBindingModel.getTitle(), playthroughAddBindingModel.getVideoUrl(), playthroughAddBindingModel.getDescription());

        return "redirect:/games/{gameId}";

    }


}
