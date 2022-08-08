package com.gamevision.web;

import com.gamevision.model.entity.*;
import com.gamevision.model.enums.GenreNameEnum;
import com.gamevision.util.TestDataUtils;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;


@SpringBootTest
@AutoConfigureMockMvc
public class GameControllerIT {

    @Autowired //autowire all entities, then assign to them the ready entities from TestDataUtils
    private MockMvc mockMvc; //for sending HTTP requests to the test server TODO cannot autowire?!?
    private TestDataUtils testDataUtils;
    private UserEntity testUser, testAdmin;
    private GenreEntity testGenre;
    private PlaythroughEntity testPlaythrough;
    private GameEntity testGame;
    private CommentEntity testComment;


    @BeforeEach
    void setUp() { //grab ready data from TestDataUtils and assign it to the test entities
        testUser = testDataUtils.createTestUser("Game Controller Test User");
        testAdmin = testDataUtils.createTestAdmin("Game Controller Test Admin");
        testPlaythrough = testDataUtils.createTestPlaythrough(testAdmin);
        testGame = testDataUtils.createTestGame(testAdmin, Set.of(testGenre.setName(GenreNameEnum.RPG), testGenre.setName(GenreNameEnum.AA)));
    }


}
