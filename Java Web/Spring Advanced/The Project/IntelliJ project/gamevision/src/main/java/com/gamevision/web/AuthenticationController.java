package com.gamevision.web;

import com.gamevision.model.binding.UserRegisterBindingModel;
import com.gamevision.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;

@Controller
public class AuthenticationController { //REGISTER AND LOGIN
    private final UserService userService;
    private final ModelMapper modelMapper; //TODO check if necessary

    public AuthenticationController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/users/register")
    public String register() {
        return "register";
    }


    @PostMapping("/users/register")
    public String registerSubmit(@Valid UserRegisterBindingModel userRegisterBindingModel, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        //in constr (@UserRegisterBindingModel bm, BindingResult bindingResult, RedirectAttributes redirectAttributes
        boolean isUserNameFree = userService.isUserNameFree(userRegisterBindingModel.getUsername());
        boolean isEmailFree = userService.isEmailFree(userRegisterBindingModel.getEmail());
        if (bindingResult.hasErrors() || !userRegisterBindingModel.getPassword().equals(userRegisterBindingModel.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("userRegisterBindingModel", userRegisterBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userRegisterBindingModel", bindingResult);

            return "redirect:/users/register";


        } else if (!isUserNameFree) {
            redirectAttributes.addFlashAttribute("usernameTaken", true);
            redirectAttributes.addFlashAttribute("userRegisterBindingModel", userRegisterBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userRegisterBindingModel", bindingResult);
            return "redirect:/users/register";
        } else if (!isEmailFree) {
            redirectAttributes.addFlashAttribute("emailTaken", true);
            redirectAttributes.addFlashAttribute("userRegisterBindingModel", userRegisterBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userRegisterBindingModel", bindingResult);
            return "redirect:/users/register";
        }


        //Uses UserService, Authentication is separate only in controllers to avoid UserController getting too fat
        // userService.registerUser(modelMapper.map(userRegisterBindingModel, UserServiceModel.class));
        userService.registerAndLogin(userRegisterBindingModel);

        return "redirect:/";
    }


    //Model attribute for register
    @ModelAttribute
    public UserRegisterBindingModel userRegisterBindingModel() {
        return new UserRegisterBindingModel();
    }

    //==================================================================
//Login - just this, SS should handle the rest, no login in UserServiceImpl/AuthenticationService
    @GetMapping("/users/login")
    public String login() {
        return "login";
    }

    //TODO: if logged in successfully, weird redirect to http://localhost:8080/ny.jpg


    //Takes care for login errors
    //Should be post mapping!
    @PostMapping("/users/login-error")
    public String onFailedLogin(
            @ModelAttribute(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY) String username,
            RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY, username);
        redirectAttributes.addFlashAttribute("bad_credentials", true);

        return "redirect:/users/login";
    }

    //Better put this in UserController
    @GetMapping("/users/profile")//Principal (userdata for current user) - from SS, Model from Spring
    public String profile(Principal principal, Model model) {
//TODO

        return "user-profile";

    }


}
