package com.gamevision.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ModelAndView;

@Configuration
public class GamevisionBeanConfiguration {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public ModelAndView modelAndView() {
        return new ModelAndView();
    }

    //still cannot delete playthrough with this
    // @Bean
    // public WebMvcConfigurer corsConfigurer() {
    //     return new WebMvcConfigurer() {
    //         @Override
    //         public void addCorsMappings(CorsRegistry registry) {
    //             registry.addMapping("/games/{gameId}/playthroughs/{playthroughId}/delete").allowedOrigins("http://localhost:9000");
    //         }
    //     };
//
    //     //games/{gameId}/playthroughs/{playthroughId}/delete
    // }

}
