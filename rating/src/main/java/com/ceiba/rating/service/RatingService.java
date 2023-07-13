package com.ceiba.rating.service;

import com.ceiba.rating.model.Rating;
import com.ceiba.rating.model.Stars;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RatingService {

    private List<Rating> ratings = Stream.of(
            new Rating(1L, 2L, Stars.FIVE),
            new Rating(2L, 1L, Stars.ONE),
            new Rating(3L, 5L, Stars.FIVE),
            new Rating(4L, 4L, Stars.THREE),
            new Rating(5L, 3L, Stars.TWO)
    ).collect(Collectors.toList());


    public Rating createRating(Rating rating) {
        if (anyMatch(rating.getId())) {
            return null;
        }

        this.ratings.add(rating);
        return rating;
    }

    public Rating updateRating(Long id, Rating rating) {
        if (!anyMatch(id)) {
            return null;
        }

        int index = ratings.indexOf(getById(id));
        this.ratings.set(index, rating);
        return rating;
    }

    public List<Rating> findAllRatings() {
        return this.ratings;
    }

    public List<Rating> findRatingsByBookId(Long bookId) {
        return ratings.stream().filter(rating -> rating.getBookId() == bookId)
                .collect(Collectors.toList());
    }

    public void deleteRating(Long id) {
        if (!anyMatch(id)) {
            return;
        }

        this.ratings.removeIf(rating -> rating.getId().equals(id));
    }

    private boolean anyMatch(Long id) {
        return ratings.stream().anyMatch(r -> r.getId().equals(id));
    }

    private Rating getById(Long id) {
        return ratings.stream().filter(rating -> rating.getId() == id)
                .findFirst().get();
    }
}
