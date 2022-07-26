package com.gamevision.model.view;

public class CommentViewModel {
    private Long id;
    private Long userId;
    private String username;
    private String text;
    private int likesCounter;

    public CommentViewModel() {
    }

    public Long getId() {
        return id;
    }

    public CommentViewModel setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public CommentViewModel setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public CommentViewModel setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getText() {
        return text;
    }

    public CommentViewModel setText(String text) {
        this.text = text;
        return this;
    }

    public int getLikesCounter() {
        return likesCounter;
    }

    public CommentViewModel setLikesCounter(int likesCounter) {
        this.likesCounter = likesCounter;
        return this;
    }
}
