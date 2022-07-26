package com.gamevision.service;

import com.gamevision.model.entity.GameEntity;
import com.gamevision.model.view.PlaythroughViewModel;

import java.util.List;

public interface PlaythroughService {
    void addPlaythrough(Long gameId, String title, String url, String description);

    List<PlaythroughViewModel> getAllPlaythroughsForGame(Long gameId);
}
