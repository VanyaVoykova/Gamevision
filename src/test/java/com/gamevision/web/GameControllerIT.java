package com.gamevision.web;

import com.gamevision.model.entity.GameEntity;
import com.gamevision.model.entity.UserEntity;
import com.gamevision.util.TestDataUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class GameControllerIT {

    @Autowired
    private MockMvc mockMvc; //for sending HTTP requests to the test server


    @Autowired
    private TestDataUtils testDataUtils;
    private UserEntity testUser, testAdmin;
    private GameEntity testGame, testAdminGame;



}
