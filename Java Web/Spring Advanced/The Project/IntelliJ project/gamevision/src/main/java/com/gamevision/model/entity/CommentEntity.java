package com.gamevision.model.entity;

import javax.persistence.*;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Entity
@Table(name = "comments")
public class CommentEntity extends BaseEntity {

    @ManyToOne
    private UserEntity author;

  // @Size(min = 3, max = 30)
  // private String title; //optional

    @Column(nullable = false)
    @Size(min = 10, max = 3000)
    private String text;

    @PositiveOrZero
    private int likesCounter;

    public CommentEntity() {
    }

    public UserEntity getAuthor() {
        return author;
    }

    public CommentEntity setAuthor(UserEntity author) {
        this.author = author;
        return this;
    }

   // public String getTitle() {
   //     return title;
   // }
//
   // public CommentEntity setTitle(String title) {
   //     this.title = title;
   //     return this;
   // }

    public String getText() {
        return text;
    }

    public CommentEntity setText(String text) {
        this.text = text;
        return this;
    }

    public int getLikesCounter() {
        return likesCounter;
    }

    public CommentEntity setLikesCounter(int likesCounter) {
        this.likesCounter = likesCounter;
        return this;
    }
}

//TODO: 2 types of comments: 1. for a game 2. general comments (for a topic, but topics are not currently implemented)
//Make this a base class for all comments, then create heirs for cases 1 and 2