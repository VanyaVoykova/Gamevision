package com.gamevision.service.impl;

import com.gamevision.exceptions.GameNotFoundException;
import com.gamevision.exceptions.GameTitleExistsException;
import com.gamevision.exceptions.UserNotFoundException;
import com.gamevision.model.entity.GameEntity;
import com.gamevision.model.entity.GenreEntity;
import com.gamevision.model.entity.UserEntity;
import com.gamevision.model.enums.GenreNameEnum;
import com.gamevision.model.servicemodels.GameAddServiceModel;
import com.gamevision.model.servicemodels.GameEditServiceModel;
import com.gamevision.model.view.GameCardViewModel;
import com.gamevision.model.view.GameViewModel;
import com.gamevision.repository.GameRepository;
import com.gamevision.repository.GenreRepository;
import com.gamevision.repository.UserRepository;
import com.gamevision.service.GameService;
import com.gamevision.service.PlaythroughService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GameServiceImpl implements GameService {
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final PlaythroughService playthroughService;
    private final ModelMapper modelMapper;

    public GameServiceImpl(GameRepository gameRepository, UserRepository userRepository, GenreRepository genreRepository, ModelMapper modelMapper, PlaythroughService playthroughService) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.genreRepository = genreRepository;
        this.playthroughService = playthroughService;
        this.modelMapper = modelMapper;
    }

    //Skip pagination for now
    @Override
    public Page<GameCardViewModel> getAllGames(Pageable pageable) { //Page, not list!!!
        //TODO: check if Limit description length for game cards works correctly
        //no stream, no collect to list, returning Page instead
        return gameRepository.findAll(pageable)
                .map(this::mapGameEntityToCardView);
    }

    @Override
    public void addGame(GameAddServiceModel gameAddServiceModel) {
        //Check if title doesn't already exist - must be unique!
        GameEntity existingGameWithTitle = gameRepository.findByTitle(gameAddServiceModel.getTitle()).orElse(null);
        if (existingGameWithTitle != null) { //game with that name EXISTS
            throw new GameTitleExistsException(); //"A game with that title already exists."
        }

        UserEntity addedByUser = userRepository.findByUsername(gameAddServiceModel.getAddedBy()).orElseThrow(UserNotFoundException::new);

        GameEntity gameToAdd = modelMapper.map(gameAddServiceModel, GameEntity.class);

        gameToAdd.setAddedBy(addedByUser);

        //List<String> in SM -> Set<GenreEntity> in GameEntity; list in SM never empty
        Set<GenreEntity> genres = new LinkedHashSet<>(); //LHS to keep them ordered as they appear in the enum for consistency - easy to compare games visually
        for (String genreName : gameAddServiceModel.getGenres()) {
            GenreEntity genreEntity = genreRepository.findByName(GenreNameEnum.valueOf(genreName)); //entity's name is GenreNameEnum - RPG(Role-playing), ....
            genres.add(genreEntity);
        }

        gameToAdd.setGenres(genres); //added all at once
        //ADD EMPTY COLLECTIONS or they are null, MM won't initialize empty collections!!!
        gameToAdd.setPlaythroughs(new HashSet<>());
        gameToAdd.setComments(new LinkedHashSet<>()); //Linked to keep order of addition

        //TODO probably one final try/catch if something still goes wrong - e.g. description somehow too long (GameEntity's description has @Column(columnDefinition = "TEXT"))
        gameRepository.save(gameToAdd); //so that GameRepository grants it an ID - PlaythroughEntity has to be created with a gameID

        GameEntity addedGameFromRepo = gameRepository.findByTitle(gameToAdd.getTitle()).orElseThrow(GameNotFoundException::new); //shouldn't be null

        //playthrough data should be validated by the GameAddBindingModel, it holds both game and playthrough data...

        //Adds playthrough to PlaythroughRepository, then gets the newly added game from the repo and adds the playthrough to it; throws exception when game not found
        playthroughService.addPlaythrough(addedGameFromRepo.getId(), gameAddServiceModel.getPlaythroughTitle(), gameAddServiceModel.getPlaythroughVideoUrl(), gameAddServiceModel.getPlaythroughDescription()); //add UserDetails in service

    }

    @Override //Doesn't include playthroughs
    public void editGame(Long gameId, GameEditServiceModel gameEditServiceModel) {
        //We need the id!!
        // 1. Pull orignal GameEntity to be edited from the repo
        GameEntity gameToEdit = gameRepository.findById(gameId).orElseThrow(GameNotFoundException::new);
        // 2. check the new title - if another gameEntity (with ANOTHER id) does not have it in order to keep titles UNIQUE
        GameEntity existingGameWithNewTitle = gameRepository.findByTitle(gameEditServiceModel.getTitle()).orElse(null); //if null -> OK, proceed

        if (existingGameWithNewTitle != null) {
            throw new GameTitleExistsException(); //has static final message
        }

        //Clear to go, set new fields
        gameToEdit.setTitle(gameEditServiceModel.getTitle())
                .setTitleImageUrl(gameEditServiceModel.getTitleImageUrl())
                .setDescription(gameEditServiceModel.getDescription());

        //private List<String> genres; -> Set<GenreEntity>

        gameToEdit.getGenres().clear(); //Clear the existing genres first //entity getters don't return unmodifiable collections AFAIK
        for (String genreName : gameEditServiceModel.getGenres()) {
            GenreEntity genreEntity = genreRepository.findByName(GenreNameEnum.valueOf(genreName)); //entity's name is GenreNameEnum - RPG(Role-playing), ....
            gameToEdit.getGenres().add(genreEntity);
        }

        gameRepository.save(gameToEdit); //don't forget to update the entity
    }

    @Override
    public GameEntity getGameByTitle(String gameTitle) {
        return gameRepository.findByTitle(gameTitle).orElseThrow(GameNotFoundException::new);
    }

    @Override
    public Long getGameIdByTitle(String gameTitle) {
        GameEntity game = gameRepository.findByTitle(gameTitle).orElseThrow(GameNotFoundException::new);
        return game.getId();
    }

    @Override
    public GameViewModel getGameViewModelById(Long id) {
        GameEntity gameEntity = gameRepository.findById(id).orElseThrow(GameNotFoundException::new);
        GameViewModel gameViewModel = modelMapper.map(gameEntity, GameViewModel.class);

        //Map the Set<GenreEntity> to a List<String> for the view model
        List<String> genresAsStrings = gameEntity.getGenres()
                .stream()
                .map(genreEntity -> genreEntity.getName().getGenreName())
                .collect(Collectors.toList());

        gameViewModel.setGenres(genresAsStrings);

        return gameViewModel;


    }

    @Override
    public String getGameTitleById(Long id) {
        GameEntity game = gameRepository.findById(id).orElseThrow(GameNotFoundException::new);
        return game.getTitle();
    }


    private GameCardViewModel mapGameEntityToCardView(GameEntity gameEntity) {
        GameCardViewModel gameCardView = modelMapper.map(gameEntity, GameCardViewModel.class);
        List<String> genresAsStrings = gameEntity.getGenres()
                .stream()
                .map(genreEntity -> genreEntity.getName().getGenreName())
                .collect(Collectors.toList());
        gameCardView.setGenres(genresAsStrings);

        //GenreEntity to List<String>


        //Prevent out of bounds when description length is shorter
        int maxLength = Math.min(gameEntity.getDescription().length(), 400);

        //ViewModel's .getDescription() will be null when pre-mapping, get it from the entity and set it separately
        gameCardView.setDescription(gameEntity.getDescription().substring(0, maxLength) + "..."); //OPTIMIZATION: tweak visualization, cut at last whitespace
        return gameCardView;
    }


}
