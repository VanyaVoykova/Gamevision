package com.gamevision.util;

import com.gamevision.model.entity.*;
import com.gamevision.model.enums.GenreNameEnum;
import com.gamevision.model.enums.UserRoleEnum;
import com.gamevision.repository.*;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class TestDataUtils {
    private UserRepository userRepository;
    private UserRoleRepository userRoleRepository;
    private GameRepository gameRepository;
    private GenreRepository genreRepository;
    private PlaythroughRepository playthroughRepository;
    private CommentRepository commentRepository;

    public TestDataUtils(UserRepository userRepository, UserRoleRepository userRoleRepository, GameRepository gameRepository, GenreRepository genreRepository, PlaythroughRepository playthroughRepository, CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.gameRepository = gameRepository;
        this.genreRepository = genreRepository;
        this.playthroughRepository = playthroughRepository;
        this.commentRepository = commentRepository;
    }


    private void initRoles() {
        if (userRoleRepository.count() == 0) {
            UserRoleEntity adminRole = new UserRoleEntity().setName(UserRoleEnum.ADMIN);
            UserRoleEntity userRole = new UserRoleEntity().setName(UserRoleEnum.USER);

            userRoleRepository.save(adminRole);
            userRoleRepository.save(userRole);
        }
    }

    //no password
    public UserEntity createTestAdmin(String username) {
        initRoles();
        UserEntity admin = new UserEntity()
                .setUsername(username)
                .setEmail("admin@test.com")
                .setActive(true)
                .setUserRoles(new HashSet<>(userRoleRepository.findAll()));

        return userRepository.save(admin);

    }


    public UserEntity createTestUser(String username) {
        initRoles();
        UserEntity admin = new UserEntity()
                .setUsername(username)
                .setEmail("admin@test.com")
                .setActive(true)
                .setUserRoles(Set.of(new UserRoleEntity().setName(UserRoleEnum.USER)));

        return userRepository.save(admin);

    }

    public GameEntity createTestGame(UserEntity addedBy, Set<GenreEntity> genres) {
        GameEntity testGame = new GameEntity()
                .setTitle("Test Game Title")
                .setTitleImageUrl("Test Title Image URL")
                .setDescription("Test game description")
                .setGenres(Set.of(new GenreEntity().setName(GenreNameEnum.RPG),
                        new GenreEntity().setName(GenreNameEnum.AA)))
                .setPlaythroughs(Set.of(createTestPlaythrough(addedBy)))
                .setComments(new HashSet<>());
        return gameRepository.save(testGame);
    }

    public PlaythroughEntity createTestPlaythrough(UserEntity addedBy) {
        PlaythroughEntity testPlaythrough = new PlaythroughEntity()
                .setTitle("Test Playthrough Title")
                .setVideoUrl("Test video URL")
                .setDescription("Test Playthrough Description");

        return playthroughRepository.save(testPlaythrough);
    }

    public void wipeDatabase() {
        userRepository.deleteAll();
        playthroughRepository.deleteAll(); //to avoid orphaned playthroughs without a game
        gameRepository.deleteAll();
    }


}
