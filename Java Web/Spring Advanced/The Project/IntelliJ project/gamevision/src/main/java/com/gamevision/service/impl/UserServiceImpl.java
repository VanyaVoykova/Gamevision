package com.gamevision.service.impl;

import com.gamevision.errorhandling.exceptions.UserNotFoundException;
import com.gamevision.model.binding.UserRegisterBindingModel;
import com.gamevision.model.entity.*;
//import com.gamevision.model.mappers.UserMapper;
import com.gamevision.model.enums.UserRoleEnum;
import com.gamevision.model.servicemodels.UserRegisterServiceModel;
import com.gamevision.repository.ProfilePictureRepository;
import com.gamevision.repository.UserRepository;
import com.gamevision.repository.UserRoleRepository;
import com.gamevision.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

//No separate AuthenticationService, register is here, login is handled
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService appUserDetailsService;
    private final ProfilePictureRepository profilePictureRepository; //no service, as barely any logic is required
    private final String adminPass;
    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository,
                           UserRoleRepository userRoleRepository,
                           PasswordEncoder passwordEncoder,
                           UserDetailsService appUserDetailsService,
                           ProfilePictureRepository profilePictureRepository, @Value("${spring.security.user.password}") String adminPass, ModelMapper modelMapper) { //from application.properties; there is also a default pass shown in the console when you start the app
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.appUserDetailsService = appUserDetailsService;
        this.profilePictureRepository = profilePictureRepository;
        this.adminPass = adminPass;
        this.modelMapper = modelMapper;
    }


    public void registerAndLogin(UserRegisterBindingModel userRegisterBindingModel) {
        UserRegisterServiceModel newUserSM = modelMapper.map(userRegisterBindingModel, UserRegisterServiceModel.class);
        UserEntity newUser = modelMapper.map(newUserSM, UserEntity.class);

        //Encode the password or SS will be angry
        newUser.setPassword(passwordEncoder.encode(userRegisterBindingModel.getPassword()));

        //Set default profile picture
        ProfilePicture defaultPic = new ProfilePicture();
        defaultPic.setUrl("/static/images/default-blank-profile-picture-640x640.png");
        //Save the ProfilePicture to the DB first!!!! Otherwise: object references an unsaved transient instance - save the transient instance before flushing error!
        profilePictureRepository.save(defaultPic);

        newUser.setProfilePicture(defaultPic);

        //Get it from the REPOOOOOo
        UserRoleEntity defaultRole = userRoleRepository.findByName(UserRoleEnum.USER).orElse(null); //should never be null
        assert defaultRole != null; // to keep the IDE happy
        newUser.setUserRoles(List.of(defaultRole));

        newUser.setActive(true);


        userRepository.save(newUser);

        //TODO: NOTE how we set the authentication for the context and LOGIN the user
        UserDetails userDetails =
                appUserDetailsService.loadUserByUsername(newUser.getUsername());

        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        userDetails.getPassword(),
                        userDetails.getAuthorities()
                );

        SecurityContextHolder.
                getContext()
                .setAuthentication(auth);

    }


    //=============================================
    //Initialization

    //The default pass adminPass is "admin"

    public void initUsers() {
        if (userRepository.count() == 0) {
            //These are hardcoded and initialized in the DB, get them from repo
            UserRoleEntity adminRole = userRoleRepository.findByName(UserRoleEnum.ADMIN).orElse(null); //should never be null
            assert adminRole != null; // to keep the IDE happy

            UserRoleEntity moderatorRole = userRoleRepository.findByName(UserRoleEnum.MODERATOR).orElse(null); //should never be null
            assert moderatorRole != null; // to keep the IDE happy


            initAdmin(List.of(adminRole, moderatorRole));
            initModerator(List.of(moderatorRole));
            initUser(List.of());
        }
    }

    private void initAdmin(List<UserRoleEntity> roles) {
        UserEntity admin = new UserEntity()
                .setUsername("Admin")
                .setPassword(passwordEncoder.encode(adminPass))
                .setEmail("admin@example.com")
                .setUserRoles(roles);

        //Set the rest of the fields - WET but not much time to fix TODO: clean up, DRY
        ProfilePicture adminPic = new ProfilePicture();
        adminPic.setUrl("/static/images/default-blank-profile-picture-640x640.png"); //TODO get a different default pic for admins
        //Save the ProfilePicture to the DB first!!!! Otherwise: "object references an unsaved transient instance - save the transient instance before flushing" error!
        profilePictureRepository.save(adminPic);

        admin.setProfilePicture(adminPic);

        admin.setActive(true);
        admin.setGames(new HashSet<>());
        admin.setFavouritePlaythroughs(new HashSet<>());


        userRepository.save(admin);
    }

    private void initModerator(List<UserRoleEntity> roles) {
        UserEntity moderator = new UserEntity()
                .setUsername("Moderator")
                .setPassword(passwordEncoder.encode(adminPass))
                .setEmail("moderator@example.com")
                .setUserRoles(roles);

        //Set the rest of the fields
        ProfilePicture moderatorPic = new ProfilePicture();
        moderatorPic.setUrl("/static/images/default-blank-profile-picture-640x640.png"); //TODO get a different default pic for admins
        //Save the ProfilePicture to the DB first!!!! Otherwise: object references an unsaved transient instance - save the transient instance before flushing error!
        profilePictureRepository.save(moderatorPic);

        moderator.setProfilePicture(moderatorPic);

        moderator.setActive(true);
        moderator.setGames(new HashSet<>());
        moderator.setFavouritePlaythroughs(new HashSet<>());


        userRepository.save(moderator);
    }

    private void initUser(List<UserRoleEntity> roles) {
        UserEntity user = new UserEntity()
                .setUserRoles(roles)
                .setUsername("User")
                .setEmail("user@example.com")
                .setPassword(passwordEncoder.encode(adminPass));


        //Set the rest of the fields
        ProfilePicture userPic = new ProfilePicture();
        userPic.setUrl("/static/images/default-blank-profile-picture-640x640.png"); //TODO: get a different default pic for admins
        profilePictureRepository.save(userPic);

        user.setProfilePicture(userPic);

        user.setActive(true);
        user.setGames(new HashSet<>());
        user.setFavouritePlaythroughs(new HashSet<>());

        userRepository.save(user);
    }


    //===========================================
    //Auxiliary methods

    //TODO: check if throwing is needed

    @Override
    public boolean isUserNameFree(String username) {
        return userRepository.findByUsernameIgnoreCase(username).isEmpty();
    }

    @Override
    public boolean isEmailFree(String email) {
        return userRepository.findByEmailIgnoreCase(email).isEmpty();
    }

    @Override
    public UserEntity findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public UserEntity findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
    }


}
