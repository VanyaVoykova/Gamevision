package com.gamevision.service;

import com.gamevision.model.entity.GameEntity;
import com.gamevision.model.servicemodels.GameAddServiceModel;
import com.gamevision.model.servicemodels.GameEditServiceModel;
import com.gamevision.model.view.GameCardViewModel;
import com.gamevision.model.view.GameViewModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface GameService {
    Page<GameCardViewModel> getAllGames(Pageable pageable);

    void addGame(GameAddServiceModel gameAddServiceModel);
    void editGame(Long gameId, GameEditServiceModel gameEditServiceModel);

    GameEntity getGameByTitle(String gameTitle);

    Long getGameIdByTitle(String gameTitle);

    GameViewModel getGameViewModelById(Long id);

    String getGameTitleById(Long id);

}
