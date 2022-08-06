package com.gamevision.errorhandling;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Order(2)
@ControllerAdvice(annotations = Controller.class) //for regular Controllers
public class GamevisionControllerExceptionHandler {


    @ExceptionHandler({Exception.class})
    public String handleError(Exception e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorCause", e.getCause());
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        System.out.println(e.getCause().toString());
        System.out.println(e.getMessage());
       // Throwable cause = e.getCause();
        return "error"; //changed from "redirect:error"
    }

}

//Backup of the original
//  @ExceptionHandler({Exception.class})
//    public String handleError() {
//        return "error"; //changed from "redirect:error"
//    }
