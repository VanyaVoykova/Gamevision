package com.gamevision.service.impl;

import com.gamevision.errorhandling.exceptions.GameNotFoundException;
import com.gamevision.model.entity.GameEntity;
import com.gamevision.model.entity.PlaythroughEntity;
import com.gamevision.model.entity.UserEntity;
import com.gamevision.model.view.CommentViewModel;
import com.gamevision.model.view.PlaythroughViewModel;
import com.gamevision.repository.GameRepository;
import com.gamevision.repository.PlaythroughRepository;
import com.gamevision.repository.UserRepository;
import com.gamevision.service.PlaythroughService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaythroughServiceImpl implements PlaythroughService {
    private final PlaythroughRepository playthroughRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public PlaythroughServiceImpl(PlaythroughRepository playthroughRepository, GameRepository gameRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.playthroughRepository = playthroughRepository;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public void addPlaythrough(Long gameId, String title, String url, String description) {

        GameEntity game = gameRepository.findById(gameId).orElseThrow(GameNotFoundException::new); //OPTIMIZATION: better call the GameService...
        UserEntity addedByUser = game.getAddedBy();

        PlaythroughEntity playthrough = new PlaythroughEntity();
        //initialize Set<CommentEntity> comments  and  private Integer likesCounter; in the @Service!
        playthrough
                .setGame(game)
                .setTitle(title)
                .setVideoUrl(url)
                .setDescription(description)
                .setAddedBy(addedByUser)
                .setComments(new LinkedHashSet<>())
                .setLikesCounter(0);

        playthroughRepository.save(playthrough);

        game.getPlaythroughs().add(playthrough);
        gameRepository.save(game); //HAVE TO "UPDATE" the GameEntity like THIS!!!
    }

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

            playthroughs.add(viewModel);

        }

        return playthroughs;

    }


}