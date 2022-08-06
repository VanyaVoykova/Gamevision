package com.gamevision.service.impl;

import com.gamevision.errorhandling.exceptions.GameNotFoundException;
import com.gamevision.errorhandling.exceptions.UserNotFoundException;
import com.gamevision.model.binding.PlaythroughAddBindingModel;
import com.gamevision.model.entity.GameEntity;
import com.gamevision.model.entity.PlaythroughEntity;
import com.gamevision.model.entity.UserEntity;
import com.gamevision.model.user.GamevisionUserDetails;
import com.gamevision.model.view.CommentViewModel;
import com.gamevision.model.view.PlaythroughViewModel;
import com.gamevision.repository.GameRepository;
import com.gamevision.repository.PlaythroughRepository;
import com.gamevision.repository.UserRepository;
import com.gamevision.service.PlaythroughService;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaythroughServiceImpl implements PlaythroughService {
    private final PlaythroughRepository playthroughRepository;
    private final GameRepository gameRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    public PlaythroughServiceImpl(PlaythroughRepository playthroughRepository, GameRepository gameRepository, ModelMapper modelMapper, UserRepository userRepository) {
        this.playthroughRepository = playthroughRepository;
        this.gameRepository = gameRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }


    @Override
    public void addPlaythrough(Long gameId, PlaythroughAddBindingModel playthroughAddBindingModel, String username) {


        GameEntity game = gameRepository.findById(gameId).orElseThrow(GameNotFoundException::new); //OPTIMIZATION: better call the GameService...
        UserEntity addedByUser = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new); //nope can be added by a different user from the one who added the game

        //initialize Set<CommentEntity> comments  and  private Integer likesCounter; in the @Service!
        PlaythroughEntity playthrough = new PlaythroughEntity()
                .setGame(game)
                .setTitle(playthroughAddBindingModel.getTitle())
                .setVideoUrl(playthroughAddBindingModel.getVideoUrl())
                .setDescription(playthroughAddBindingModel.getDescription())
                .setAddedBy(addedByUser)
                .setComments(new LinkedHashSet<>())
                .setLikesCounter(0);


        game.getPlaythroughs().add( playthroughRepository.save(playthrough));
        gameRepository.save(game); //HAVE TO "UPDATE" the GameEntity like THIS!!!
    }

    @Override
    public PlaythroughEntity addPlaythroughWhenAddingGame(Long gameId, String title, String videoUrl, String description, String username) {
        GameEntity game = gameRepository.findById(gameId).orElseThrow(GameNotFoundException::new); //OPTIMIZATION: better call the GameService...
        UserEntity addedByUser = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new); //nope can be added by a different user from the one who added the game


        PlaythroughEntity playthrough = new PlaythroughEntity()
                .setGame(game)
                .setTitle(title)
                .setVideoUrl(videoUrl)
                .setDescription(description)
                .setAddedBy(addedByUser);

      return  playthroughRepository.save(playthrough);

    }

    //TBI: Maybe edit some day

    @Override
    public List<PlaythroughViewModel> getAllPlaythroughsForGame(Long gameId) {
        GameEntity game = gameRepository.findById(gameId).orElseThrow(GameNotFoundException::new);
        if (game.getPlaythroughs().isEmpty()) {
            return null;
        }

        List<PlaythroughViewModel> playthroughs = new ArrayList<>();
        for (PlaythroughEntity entity : game.getPlaythroughs()) {
            PlaythroughViewModel viewModel = new PlaythroughViewModel();

            viewModel
                    .setId(entity.getId())
                    .setTitle(entity.getTitle())
                    .setVideoUrl(entity.getVideoUrl())
                    .setDescription(entity.getDescription())
                    .setLikesCounter(entity.getLikesCounter())
                    .setComments(entity.getComments().stream()
                            .map(commentEntity -> modelMapper.map(commentEntity, CommentViewModel.class)).collect(Collectors.toList()));
//TBI: comments for playthroughs
            playthroughs.add(viewModel);

        }

        return playthroughs;

    }


}