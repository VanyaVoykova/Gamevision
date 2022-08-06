package com.gamevision.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    //Just to load the admin panel because it gets opened from the navigation bar which is common for every page
    //I didn't want to load the admin-panel script with every page
    //Can likely be optimized by no time to check

    @GetMapping("/admin")
    public String loadAdminPanel(){
     //   System.out.println("@Controller for @GetMapping");
        return "admin-panel";
    }
}
