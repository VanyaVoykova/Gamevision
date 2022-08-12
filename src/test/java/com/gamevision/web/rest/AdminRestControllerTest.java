package com.gamevision.web.rest;

import com.gamevision.model.entity.UserEntity;
import com.gamevision.service.AdminService;
import com.gamevision.util.TestDataUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
//@WebMvcTest //(AdminRestController.class) //added this todo check if it gets better nope
public class AdminRestControllerTest {
//TODO: note: we plonk all that's used by the controller with @Autowired and @Autowired MockMvc

//TODO: how to mock USER and ADMIN for the same test

    //Here for better visibility and reusability
    private static final String ADMINNAME = "TestAdmin";

    //  @Autowired
    //  private PasswordEncoder passwordEncoder;
//
    //  @Autowired
    //  private UserRoleRepository userRoleRepository;
//
    //  @Autowired
    //  private UserRepository userRepository;
//


    //  private AdminRestController adminRestControllerToTest;
    private static final String USERNAME = "TestUser";
    @MockBean(name = "mockUserDetails")
    private UserDetails userDetails;
    @Autowired//todo note how we mock the service used by the controller
    private AdminService adminService; //actual service
    @Autowired
    private MockMvc mockMvc; //just for the initial view; don't forget your private access modifier...
    @Autowired
    private TestDataUtils testDataUtils;
    //Users
    private UserEntity testUser, testAdmin;

    //private static final String ADMINPASSWORD = "testpass";

    @BeforeEach
    void setUp() {
        testAdmin = testDataUtils.createTestAdmin(ADMINNAME);
        testUser = testDataUtils.createTestUser(USERNAME);

        //  mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        //Setup actual admin and user with the @Autowired repos and services
        //    UserEntity admin = new UserEntity();

        //    UserRoleEntity adminRole = userRoleRepository.findByName(UserRoleEnum.ADMIN).orElse(null);
        //    UserRoleEntity userRole = userRoleRepository.findByName(UserRoleEnum.USER).orElse(null);

        //    admin.setUsername(ADMINNAME)
        //            .setPassword(passwordEncoder.encode(ADMINPASSWORD))
        //            .setEmail("admin@example.com")
        //            .setUserRoles(Set.of(userRole, adminRole))
        //            .setActive(true)
        //            .setGames(new HashSet<>())
        //            .setFavouritePlaythroughs(new HashSet<>());

        //    testAdmin = userRepository.save(admin);


        //    //Add UserDetails
        //    List<GrantedAuthority> authorities = testAdmin.getUserRoles().stream().map(r -> {
        //        return new SimpleGrantedAuthority("ROLE_".concat(r.getName().name()));
        //    }).collect(Collectors.toList());

        //    Long userId = testAdmin.getId();
        //    this.gamevisionUserDetailsAdmin = new GamevisionUserDetails(testAdmin.getUsername(), testAdmin.getPassword(), testAdmin.getEmail(),
        //            testAdmin.isActive(), authorities);

        //    //todo USER

        //set up games and playthroughs since users are initiallized with empty collections of those entitiees

        //  GameEntity testGame = testDataUtils.createTestGame()
        //these two include initRoles()
        //Make sure data is valid and UNIQUE fields are UNIQUE, Spring may complain about user roles when it's actually the same copied email for both
        // testUser = testDataUtils.createTestUser("TestUser");
        // testAdmin = testDataUtils.createTestAdmin("TestAdmin");

        //  adminService = new AdminServiceImpl(userRepository, userRoleRepository, userService);

        //   adminRestControllerToTest = new AdminRestController(adminService); //follow the way we inject service in the controller in actual app


    }


    @AfterEach
    void tearDown() {
        testDataUtils.wipeDatabase();
        //userRepository.deleteAll();
    }


    //TODO GOT TO LOG AN ADMIN FIRST, OF COURSE......


    //Instead of login first in order to see the admin panel as it actually happens, we just test @WithMockUser
    @Test
    @WithMockUser(username = ADMINNAME)
    void adminPanelShown() throws Exception {

        //  mockMvc.perform(post("/users/login")
        //          .param("TestAdmin")
        //          .param("testAdminPass")
        //          .with(csrf()))
        //          .andExpect(redirectedUrl("/"));
//

        mockMvc.perform(get("/admin"))
                //  .andExpect(status().isOk());
                .andExpect(view().name("admin-panel")); //no ModelAndView found
        //   .andExpect(model().attributeExists("attrName")) //if we have an attribute
        //andExpect(status().isOK()) //if we have status

        //    mockMvc.perform(get("/admin")) //NOT just get, ModelAndView is returned here!

        //NOT LIKE THIS   .andExpect(view().name("admin-panel.html")); //not expecting a view but a ModelAndView
    }

    // @Test
    // @WithMockUser(username = ADMINNAME)
    // void promoteUserToAdminSuccessfulWithExistingUser() {
    //     mockMvc.perform(put("/admin/promote"))
    //             .
    // }


    //    @Test
    ////make sure to follow field order; REAL repo is used, so change name and email to keep them unique or delete User from the DB
    //    void testUserRegistrationWithValidData() throws Exception {
    //        mockMvc.perform(post("/users/register")
    //                        .param("username", "Gabriel")
    //                        .param("email", "gabe@example.com")
    //                        .param("password", "gabe")
    //                        .param("confirmPassword", "gabe")
    //                        .with(csrf()))
    //                .andExpect(redirectedUrl("/"));
    //
    //        System.out.println("MockMvc: " + mockMvc);
    //
    //    }


}
