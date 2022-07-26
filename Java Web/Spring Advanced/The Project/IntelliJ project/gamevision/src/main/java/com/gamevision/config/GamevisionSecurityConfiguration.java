package com.gamevision.config;

import com.gamevision.model.enums.UserRoleEnum;
import com.gamevision.service.GamevisionUserDetailsService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;


import com.gamevision.repository.UserRepository;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


//For some reason, the IDE cannot detect that the HttpSecurity bean is configured by Spring Boot. You can get rid of the error by adding @EnableWebSecurity to your configuration class, it solves it because the annotation imports the HttpSecurityConfiguration configuration class.
@EnableWebSecurity
@Configuration
public class GamevisionSecurityConfiguration {

    //Here we have to expose 3 things:
    // 1. PasswordEncoder @Bean
    // 2. UserDetailsService (with user repo in constructor) @Bean
    // 3. SecurityFilterChain


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Pbkdf2PasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return new GamevisionUserDetailsService(userRepository);
    }

    @Bean //defines which pages should require authentication / specific role - type http. to see them all
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // define which requests are allowed and which not
        http.authorizeRequests()
                // everyone can download static resources (css, js, images)
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                //pages everyone can access - authentication is required only for likes and comments + admin & moderator functions
                .antMatchers("/", "/about", "/games/all", "/games/{id}", "/users/forum").permitAll() ///games/view/* - view a game, * is id

                .antMatchers("/users/login", "/users/register").anonymous() //guest users only
                .antMatchers("/users/profile").authenticated() //authenticated users only

                //for authenticated users only; TODO: check if this URL needs tweaking or it's Spring lingo; will be needed only if I get to the admin panel
                .antMatchers("/pages/moderators").hasRole(UserRoleEnum.MODERATOR.name())
                .antMatchers("/pages/admins", "/games/add", "/games/{id}/edit", "/games/{id}/delete", "/games/{id}/playthroughs/all", "/games/{id}/playthroughs/all/add").hasRole(UserRoleEnum.ADMIN.name())
//TODO: add for admins - users/{userId} - user management

                //TODO add for profile
                // .antMatchers("/users/profile").authenticated()

                //All other pages available for authenticated users (aka simple users)
                .anyRequest()
                .authenticated()

                .and()
                //login <form> configuration
                .formLogin()
                // the login page with its url
                .loginPage("/users/login")
                //check what credentials are used for login, usually username and password
                // the name of the username <form> field;     //simpler alternative: userNameParemeter("username")
                .usernameParameter(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY)
                // the name of the password <form> field; naming is very important
                .passwordParameter(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_PASSWORD_KEY)
                // where to go on successful login
                .defaultSuccessUrl("/")
                // where to go in case on failed login, just a mapping in controller is enough, no separate template needed, just redirect to login
                .failureForwardUrl("/users/login-error") //("/users/login-error") or put a query param ("/users/login?error=true")

                .and()
                // configure logout
                .logout()
                //the logout url, must be POST request (remember to use POST in controller and template)
                .logoutUrl("/users/logout")
                // on logout go to the home page; It shouldn't be able to fail, right?
                .logoutSuccessUrl("/")
                // invalidate the session and delete the cookies
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");

        //.csrf().disable(); //if not useing any cors


        return http.build();
    }


}
