package com.gamevision.util;

import com.gamevision.model.entity.GameEntity;
import com.gamevision.model.entity.GenreEntity;
import com.gamevision.model.entity.UserEntity;
import com.gamevision.model.entity.UserRoleEntity;
import com.gamevision.model.enums.UserRoleEnum;
import com.gamevision.repository.*;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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

  // public GameEntity createTestGame(UserEntity addedBy, Set<GenreEntity> genres){
  //     //TODO
  // }

}
