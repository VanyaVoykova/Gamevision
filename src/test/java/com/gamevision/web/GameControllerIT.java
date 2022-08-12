package com.gamevision.web;

import com.gamevision.model.entity.CommentEntity;
import com.gamevision.model.entity.GameEntity;
import com.gamevision.model.entity.PlaythroughEntity;
import com.gamevision.model.entity.UserEntity;
import com.gamevision.model.enums.GenreNameEnum;
import com.gamevision.repository.GameRepository;
import com.gamevision.repository.GenreRepository;
import com.gamevision.repository.UserRoleRepository;
import com.gamevision.service.GameService;
import com.gamevision.util.TestDataUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@SpringBootTest
@AutoConfigureMockMvc

public class GameControllerIT {


    //autowire all necessary repos and services

    //TODO: initialize test entities here for visibility
    UserEntity testUser, testAdmin;
    GameEntity testGame;
    PlaythroughEntity testPlaythrough;
    CommentEntity testComment;


    //  @Autowired
    //  private PlaythroughRepository playthroughRepository;
    //  @Autowired
    //  private CommentRepository commentRepository;
    //roles and genres are hardcoded by nature, hence don't require additional data setup, no need to create anything more
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired //hardcoded by nature, can be used as it is
    private GenreRepository genreRepository;
    //  @Autowired
    //  private UserRepository userRepository;
    @Autowired
    private GameRepository gameRepository; //needed to check if empty for no games message
    @Autowired
    private GameService gameService; //for getGameByTitle, etc.
    //or use the prepared TestDataUtils
    @Autowired
    private TestDataUtils testDataUtils;
    //additional utils
    @Autowired //autowire all necessary repos and services, then assign to them the ready entities from TestDataUtils
    private MockMvc mockMvc; //for sending HTTP requests to the test server TODO cannot autowire?!?
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() { //saving to repos included
        testUser = testDataUtils.createTestUser("TestUser");
        testAdmin = testDataUtils.createTestAdmin("TestAdmin");
        testGame = testDataUtils.createTestGame(testAdmin);
        testPlaythrough = testDataUtils.createTestPlaythrough(testAdmin);
        testComment = testDataUtils.createTestComment(testUser);
    }

    // @AfterEach
    // public void tearDown() {
    //     this.commentRepository.deleteAll();
    //     this.playthroughRepository.deleteAll();
    //     this.gameRepository.deleteAll();
    //     this.genreRepository.deleteAll();
    //     this.userRepository.deleteAll();
    //     this.userRoleRepository.deleteAll();
    // }

    @AfterEach
    void tearDown() {
        testDataUtils.wipeDatabase();
    }


    @Test
    void allGamesViewShown() throws Exception {
        this.mockMvc
                .perform(get("/games/all"))
                .andExpect(model().attributeExists("games")) //import static method
                .andExpect(view().name("games-all"));
    }

    @Test
    void displayNoGamesMessageWhenGameRepositoryIsEmpty() throws Exception {
        gameRepository.deleteAll(); //will be hold the one test game created with setUp(), so clear it
        this.mockMvc
                .perform(get("/games/all"))
                .andExpect(model().attributeExists("noGames")) //import static method
                .andExpect(view().name("games-all"));
    }

    @Test
    public void gameViewShown() throws Exception {
        Long gameId = testGame.getId(); //Don't hardcode it here, work with the available test data!!!!!

//Id should be 1
        this.mockMvc
                .perform(get("/games/" + gameId))
                .andExpect(view().name("game"));
    }

    @Test
    public void gameNotFound() throws Exception {
        this.mockMvc
                .perform(get("/games/1001"))
                .andExpect(model().attribute("errorMessage", "Game not found."))
                .andExpect(view().name("error"));
    }


    @Test
    @WithMockUser(username = "TestAdmin", roles = "ADMIN") //don't forget the proper user or "No ModelAndView found"
    public void addGameViewShown() throws Exception {
        this.mockMvc
                .perform(get("/games/add"))
                .andExpect(model().attribute("allGenres", GenreNameEnum.values())) //the genres are shown with checkboxes
                .andExpect(view().name("game-add"));

    }


    //TODO: POST addGameSubmit


    //   @Test //constraint violation if no UserId
    //@WithMockUser(username = "TestAdmin", roles = {"USER", "ADMIN"})
    //  // @WithUserDetails(value="TestAdmin",  userDetailsServiceBeanName = "testUserDataService") //TODO USE THIS, see GitHub MobileleUserDetailsServiceTest.java
    //   public void addGameWithValidData() throws Exception {
    //       GameEntity existingGame = testGame; //this should create one game in the repo and actually fill the empty proxies
    //       System.out.println("Existing game ID: " + existingGame.getId()); //id is 3
    //       //Needs userId, but it's not contained in the mock
//
//
//
    //       Long existingGameId =  existingGame.getId(); // 8?  Maybe get the next? Is it random?
    //   //    List<String> genres = new ArrayList<>(List.of("RPG", "AA"));
    //       this.mockMvc                    //this is the input data from the BM - note how params are set; author is obviously not manually entered in the input input
    //               .perform(post("/games/add")
    //                       .param("title", "A Great Test Game") //give the values as a MAP KVP!!! Follow the BM field names for the KEYS.
    //                       .param("titleImageUrl", "testurl")
    //                      .param("description", "Test description that has to be long enough.") //should get it saved to repo
    //               .param("genre", "RPG")  //singular in the controller due to checkbox naming issues
    //               .with(csrf()))
////Won't be saved by now?!? GAME NOT FOUND???
////TODO find out how to test with more than one genre?
    //               //genres come as a List<String>
    //               // .with(csrf())) //that's for REST
    //               .andExpect(redirectedUrl("/games/" + existingGameId + 1));
    //             //  .andExpect(redirectedUrl("/games/" + gameService.getGameByTitle("A Great Test Game").getId()));//redirectedUrl, not view
    //       //  Long gameId = gameService.getGameByTitle(testGame.getTitle()).getId();
//
//
    //   }
//
    //todo: note the options we have
    //  .andExpect(model().attribute  hasErrors, has a number of errors, does not exist

//   @Test
//   void testDeleteByAnonymousUser_Forbidden() throws Exception {
//       mockMvc.perform(delete("/games/{id}/delete", testGame.getId())   //note the {id} param syntax with ,
//               .with(csrf())
//               ).
//               andExpect(status().is3xxRedirection())
//               .andExpect(redirectedUrl("/games/all"));
//       //TODO: check redirection url to login w/o schema
//   }

//   @Test
//   @WithMockUser(
//           username = "admin@example.com",
//           roles = {"ADMIN", "USER"}
//   )
//   void testDeleteByAdmin() throws Exception {
//       mockMvc.perform(delete("/offers/{id}", testOffer.getId()).
//                       with(csrf())
//               ).
//               andExpect(status().is3xxRedirection()).
//               andExpect(view().name("redirect:/offers/all"));
//   }


}
