package com.gamevision.service.impl;

import com.gamevision.errorhandling.exceptions.GameNotFoundException;
import com.gamevision.errorhandling.exceptions.GameTitleExistsException;
import com.gamevision.errorhandling.exceptions.UserNotFoundException;
import com.gamevision.model.binding.GameAddBindingModel;
import com.gamevision.model.entity.GameEntity;
import com.gamevision.model.entity.GenreEntity;
import com.gamevision.model.entity.PlaythroughEntity;
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
import org.springframework.context.annotation.Lazy;
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

    public GameServiceImpl(GameRepository gameRepository, UserRepository userRepository, GenreRepository genreRepository, @Lazy PlaythroughService playthroughService, ModelMapper modelMapper) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.genreRepository = genreRepository;
        this.playthroughService = playthroughService;
        this.modelMapper = modelMapper;
    }

    //Skip pagination for now
    //@Cacheable("games") //disabled in order to show add game functionality, or added game won't be displayed right away
    @Override
    public Page<GameCardViewModel> getAllGames(Pageable pageable) { //Page, not list!!!
        //TODO: check if Limit description length for game cards works correctly
        //no stream, no collect to list, returning Page instead
        return gameRepository.findAll(pageable)
                .map(this::mapGameEntityToCardView);
    }

    //Get 7 random games for the Home page
    // @Cacheable("carouselGames")
    @Override
    public List<GameCardViewModel> getGamesForCarousel() {
        List<GameCardViewModel> allGamesCardViews = gameRepository.findAll().stream().map(this::mapGameEntityToCardView).collect(Collectors.toList());
        Collections.shuffle(allGamesCardViews); //shuffle the indexes
        return allGamesCardViews.subList(0, 7); //get the first 7 games
    }

    //todo uncomment to activate caching
    //Clear all games cache
    // @CacheEvict(cacheNames = "games", allEntries = true)
    @Override
    public void refreshCache() {

    }

    //todo uncomment to activate caching
//Clear Home page carousel cache
    //@CacheEvict(cacheNames = "carouselGames", allEntries = true)
    @Override
    public void refreshCarouselCache() { //for the Home page carousel
    }


    @Override
    public GameAddServiceModel addGame(GameAddBindingModel gameAddbindingModel) {
        //Check if title doesn't already exist - must be unique!
        GameEntity existingGameWithTitle = gameRepository.findByTitle(gameAddbindingModel.getTitle()).orElse(null);
        if (existingGameWithTitle != null) { //game with that name EXISTS
            throw new GameTitleExistsException(); //"A game with that title already exists."
        }

        UserEntity addedByUser = userRepository.findByUsername(gameAddbindingModel.getAddedBy()).orElseThrow(UserNotFoundException::new);

        GameEntity gameToAdd = modelMapper.map(gameAddbindingModel, GameEntity.class); //maps it alright

        gameToAdd.setAddedBy(addedByUser);

        //List<String> in SM -> Set<GenreEntity> in GameEntity; list in SM never empty
        Set<GenreEntity> genres = new LinkedHashSet<>(); //LHS to keep them ordered as they appear in the enum for consistency - easy to compare games visually
        for (String genreName : gameAddbindingModel.getGenres()) {
            GenreEntity genreEntity = genreRepository.findByName(GenreNameEnum.valueOf(genreName)); //entity's name is GenreNameEnum - RPG(Role-playing), ....
            genres.add(genreEntity);
        }

        gameToAdd.setGenres(genres); //added all at once
        //ADD EMPTY COLLECTIONS or they are null, MM won't initialize empty collections!!!
        gameToAdd.setPlaythroughs(new HashSet<>());
        gameToAdd.setComments(new LinkedHashSet<>()); //Linked to keep order of addition

        //TODO probably one final try/catch if something still goes wrong - e.g. description somehow too long (GameEntity's description has @Lob
        GameEntity addedGameFromRepo = gameRepository.save(gameToAdd); //shouldn't throw unless DB  is down... then error.html should show itself


        //todo cancel adding a playthrough when adding a game
        //
        //  //playthrough data should be validated by the GameAddBindingModel, it holds both game and playthrough data...

        //  //Adds playthrough to PlaythroughRepository, then gets the newly added game from the repo and adds the playthrough to it; throws exception when game not found
        //  PlaythroughEntity savedPlaythrough = playthroughService.addPlaythroughWhenAddingGame(addedGameFromRepo.getId(), gameAddServiceModel.getPlaythroughTitle(), gameAddServiceModel.getPlaythroughVideoUrl(), gameAddServiceModel.getPlaythroughDescription(), gameAddServiceModel.getAddedBy()); //add UserDetails in service
        //  addedGameFromRepo.getPlaythroughs().add(savedPlaythrough);
        //return gameServiceModel

        //Long id, String title, String addedBy   addedGameFromRepo.getId() is NULL?!?!?!!?
        //return new GameAddServiceModel(addedGameFromRepo.getId(), addedGameFromRepo.getTitle(), addedGameFromRepo.getAddedBy().getUsername()); //return the id so the controller can compose the link to the newly added game
        GameAddServiceModel sm = new GameAddServiceModel()
                .setId(addedGameFromRepo.getId())
                .setTitle(addedGameFromRepo.getTitle())
                .setAddedBy(addedGameFromRepo.getAddedBy().getUsername());
        //what is happening above, why does it return null
        return sm; //it's not null here, holds the id and all but the controller still blows up?!?
    }

    @Override //Doesn't include playthroughs
    public void editGame(Long gameId, GameEditServiceModel gameEditServiceModel) {
        //We need the id!!
        // 1. Pull orignal GameEntity to be edited from the repo
        GameEntity gameToEdit = gameRepository.findById(gameId).orElseThrow(GameNotFoundException::new);
        // 2. check the new title - if another gameEntity (with ANOTHER id) does not have it in order to keep titles UNIQUE
        GameEntity existingGameWithSameTitleAsTheNewTitle = gameRepository.findByTitle(gameEditServiceModel.getTitle()).orElse(null); //if null -> OK, proceed

        //Ensure it's a DIFFERENT game, so you don't get "A game with that title already exists." because you found the game you want to edit (same id).
        if (existingGameWithSameTitleAsTheNewTitle != null && !Objects.equals(existingGameWithSameTitleAsTheNewTitle.getId(), gameToEdit.getId())) {
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

    @Override
    public GameEntity getGameById(Long id) {
        return gameRepository.findById(id).orElseThrow(GameNotFoundException::new);
    }

    @Override //good practice to not have another service work directly with a repo not holding its entity
    public GameEntity saveGame(GameEntity gameEntity) {

        return gameRepository.save(gameEntity);
    }

    @Override
    public void deleteGameById(Long id) {

        GameEntity entityToDelete = gameRepository.findById(id).orElseThrow(GameNotFoundException::new);
        entityToDelete.getPlaythroughs().clear(); //delete all playthroughs to ensure none are left orphaned
        gameRepository.save(entityToDelete); //update
        gameRepository.deleteById(id);
    }

    @Override
    public void removePlaythroughFromGameByGameIdAndPlaythroughId(Long gameId, Long playthroughId) {
        GameEntity gameToLoseAPlaythrough = gameRepository.findById(gameId).orElseThrow(GameNotFoundException::new);
        PlaythroughEntity playthroughToRemove = gameToLoseAPlaythrough.getPlaythroughs().stream().filter(playthrough -> playthrough.getId().equals(playthroughId)).findFirst().get();
        gameToLoseAPlaythrough.getPlaythroughs().remove(playthroughToRemove); //removed from the GameEntity's Set<PlaythroughEntity>
        gameRepository.save(gameToLoseAPlaythrough); //update game
    }


//Results in cyclic dependency
    // @Override
    // public void removePlaythroughFromPlaythroughsByGameIdAndPlaythroughId(Long gameId, Long playthroughId) {
    //     GameEntity gameToLoseAPlaythrough = gameRepository.findById(gameId).orElseThrow(GameNotFoundException::new);
    //     PlaythroughEntity playthroughToRemove = gameToLoseAPlaythrough.getPlaythroughs().stream().filter(playthrough -> playthrough.getId().equals(playthroughId)).findFirst().get();
    //     gameToLoseAPlaythrough.getPlaythroughs().remove(playthroughToRemove); //removed from the GameEntity's Set<PlaythroughEntity>
//
    //     //update the GameEntity in the repo
    //     gameRepository.save(gameToLoseAPlaythrough);
//
    // }
//

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
