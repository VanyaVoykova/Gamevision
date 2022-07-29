package com.gamevision.model.servicemodels;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class GameAddServiceModel {

    @NotBlank
    @Size(min = 2, max = 50)
    private String title;


    @NotBlank
    private String titleImageUrl;

    @NotBlank
    @Size(min = 20, max = 5000)
    private String description;


    @NotEmpty //should always receive filled list
    private List<String> genres;

    @NotNull //the admin who added the game - get by Principal id
    private String addedBy; //just username here

    //Playthroughs are added in game view to keep add game simple and avoid dumping a lot of data all at once
    //Todo check if MM initializes an empty set here
    @Size(min = 10, max = 40) //E.g. Sarcastic Dragon Age II run
    @NotBlank
    private String playthroughTitle;

    @NotBlank
    private String playthroughVideoUrl;

    @NotBlank
    @Size(min = 10, max = 200) //E.g. Sarcastic Dragon Age II run
    private String playthroughDescription;

    //MM won't initialize empty collections, so add it the List<CommentEntity> manually in the service



    public GameAddServiceModel() {
    }

    public String getTitle() {
        return title;
    }

    public GameAddServiceModel setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getTitleImageUrl() {
        return titleImageUrl;
    }

    public GameAddServiceModel setTitleImageUrl(String titleImageUrl) {
        this.titleImageUrl = titleImageUrl;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public GameAddServiceModel setDescription(String description) {
        this.description = description;
        return this;
    }

    public List<String> getGenres() {
        return genres;
    }

    public GameAddServiceModel setGenres(List<String> genres) {
        this.genres = genres;
        return this;
    }


    public String getAddedBy() {
        return addedBy;
    }

    public GameAddServiceModel setAddedBy(String username) {
        this.addedBy = username;
        return this;
    }


    public String getPlaythroughTitle() {
        return playthroughTitle;
    }

    public GameAddServiceModel setPlaythroughTitle(String playthroughTitle) {
        this.playthroughTitle = playthroughTitle;
        return this;
    }

    public String getPlaythroughVideoUrl() {
        return playthroughVideoUrl;
    }

    public GameAddServiceModel setPlaythroughVideoUrl(String playthroughVideoUrl) {
        this.playthroughVideoUrl = playthroughVideoUrl;
        return this;
    }

    public String getPlaythroughDescription() {
        return playthroughDescription;
    }

    public GameAddServiceModel setPlaythroughDescription(String playthroughDescription) {
        this.playthroughDescription = playthroughDescription;
        return this;
    }
}
