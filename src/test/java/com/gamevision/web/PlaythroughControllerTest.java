package com.gamevision.web;

import com.gamevision.model.entity.CommentEntity;
import com.gamevision.model.entity.GameEntity;
import com.gamevision.model.entity.PlaythroughEntity;
import com.gamevision.model.entity.UserEntity;
import com.gamevision.repository.GameRepository;
import com.gamevision.repository.PlaythroughRepository;
import com.gamevision.util.TestDataUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class PlaythroughControllerTest {
    UserEntity testUser, testAdmin;
    GameEntity testGame;
    PlaythroughEntity testPlaythrough;
    CommentEntity testComment;
    @Autowired //autowire all necessary repos and services, then assign to them the ready entities from TestDataUtils
    private MockMvc mockMvc;

    @Autowired
    private TestDataUtils testDataUtils;
    @Autowired
    UserDetailsService testUserDetailsService;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private PlaythroughRepository playthroughRepository;

    @BeforeEach
    public void setUp() { //saving to repos included
        testUser = testDataUtils.createTestUser("TestUser");
        testAdmin = testDataUtils.createTestAdmin("TestAdmin");
        testGame = testDataUtils.createTestGame(testAdmin);
        testPlaythrough = testDataUtils.createTestPlaythrough(testGame, testAdmin);
        testComment = testDataUtils.createTestComment(testUser);
    }

    @AfterEach
    public void tearDown() {
        testDataUtils.wipeDatabase();
    }


    @Test
    void showPlaythroughsForGame() throws Exception {
        PlaythroughEntity pt = testDataUtils.createTestPlaythrough(testGame, testAdmin);
        testGame.getPlaythroughs().add(testPlaythrough);
        gameRepository.save(testGame); //update
        Long testGameId = testGame.getId();
        this.mockMvc.perform(get("/games/" + testGameId + "/playthroughs/all"))
                .andExpect(view().name("playthroughs-all"));

    }


    //no PT: /errors/playthroughs-not-found-error

    @Test
    void showsGameNotFoundWithNonexistentGame() throws Exception {
        this.mockMvc.perform(get("/games/11/playthroughs/all"))
                // .andExpect(model().attribute("gameNotFound", "Game not found."))
                .andExpect(view().name("/errors/game-not-found-error"));

    }


    @Test
    void showsPlaythroughsNotFoundWhenGameHasNone() throws Exception {
        GameEntity gameWithoutPlaythroughs = testDataUtils.createTestGame(testAdmin);
        Long id = gameWithoutPlaythroughs.getId();
        this.mockMvc
                .perform(get("/games/" + id + "/playthroughs/all"))
                .andExpect(view().name("/errors/playthroughs-not-found-error"));
    }

}
