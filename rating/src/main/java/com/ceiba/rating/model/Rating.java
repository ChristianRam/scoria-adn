package com.ceiba.rating.model;


public class Rating {

    private Long id;
    private Long bookId;
    private Stars stars;

    public Rating() {}

    public Rating(Long id, Long bookId, Stars stars) {
        this.id = id;
        this.bookId = bookId;
        this.stars = stars;
    }

    public Long getId() {
        return id;
    }

    public Long getBookId() {
        return bookId;
    }

    public Stars getStars() {
        return stars;
    }
}