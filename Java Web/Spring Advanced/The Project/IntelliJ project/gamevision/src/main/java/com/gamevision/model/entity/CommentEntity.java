package com.gamevision.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Entity
@Table(name = "comments")
public class CommentEntity extends BaseEntity {

    @ManyToOne
    private UserEntity author;

  // @Size(min = 3, max = 30)
  // private String title; //optional

    @Column(columnDefinition = "TEXT")
    @Size(min = 10)
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
