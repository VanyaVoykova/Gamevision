package com.gamevision.web;

import com.gamevision.errorhandling.exceptions.GameNotFoundException;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
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
System.out.println("No plythroughs? This is before the try-catch in /all");

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
       //Get the game object to visualize title, etc.
        try {
            String gameTitle = gameService.getGameTitleById(gameId); //throws GameNotFoundException but it shouldn't hit it unless someone tampers with the URL
            model.addAttribute("gameTitle", gameTitle);
        } catch (GameNotFoundException ex){
            model.addAttribute("exceptionMessage", ex.getMessage()); //or with model???
            return "playthrough-add";
        }


        System.out.println("Playthrough /add loaded");
        return "playthrough-add";
    }


    @PostMapping("/add")
    public String addPlaythroughSubmit(@PathVariable("gameId") Long gameId, @Valid PlaythroughAddBindingModel playthroughAddBindingModel,
                                       BindingResult bindingResult, RedirectAttributes redirectAttributes, @AuthenticationPrincipal GamevisionUserDetails userDetails,  Model model) {
System.out.println("POST /add: ");
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("playthroughAddBindingModel", playthroughAddBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.playthroughAddBindingModel", bindingResult);
            return "redirect:/games/" + gameId + "/playthroughs/add"; //just to be sure it's the correct url
        }

        try {
//fields are filled correctly, add the gameId that comes from the URL; UserDetails should be available in the @Service to get the username


            //  playthroughAddBindingModel.setUsername(userDetails.getUsername());
            playthroughService.addPlaythrough(gameId, playthroughAddBindingModel, userDetails.getUsername());
        } catch (Exception e) {
            model.addAttribute("gameNotFound", "Game not found.");
 return "/errors/game-not-found-error"; //checking only for existing game here, so it should be this error  todo check
          //  return "redirect:/games/" + gameId + "/playthroughs/add";
            //model.addAttribute("exceptionMessage", e.getMessage()); //or with model???
        }

//Game found, game title added to model, forge ahead



        return "redirect:/games/" + gameId; ///no {}, the actual id

    }

   @ModelAttribute("playthroughAddBindingModel")
   public PlaythroughAddBindingModel playthroughAddBindingModel() {
       return new PlaythroughAddBindingModel();
   }


}
