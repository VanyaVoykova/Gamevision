package com.gamevision.model.binding;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


public class PlaythroughAddBindingModel {

    private Long gameId; //from the @PathVariable in the URL

    @Size(min = 10, max = 40) //E.g. Sarcastic Dragon Age II run
    @NotBlank
    @NotNull
    private String title;

    @NotNull
    @NotBlank
    private String videoUrl;

    @NotBlank
    @Size(min = 10, max = 200) //E.g. Sarcastic Dragon Age II run
    private String description;

    @NotNull
    private String username; //from UserDetails

   //initialize Set<CommentEntity> comments  and  private Integer likesCounter; in the @Service!


    public PlaythroughAddBindingModel() {
    }

    public Long getGameId() {
        return gameId;
    }

    public PlaythroughAddBindingModel setGameId(Long gameId) {
        this.gameId = gameId;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public PlaythroughAddBindingModel setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public PlaythroughAddBindingModel setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public PlaythroughAddBindingModel setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public PlaythroughAddBindingModel setUsername(String username) {
        this.username = username;
        return this;
    }
}
