package com.ceiba.rating.service;

import com.ceiba.rating.exceptions.AlreadyExistException;
import com.ceiba.rating.exceptions.NotFoundException;
import com.ceiba.rating.model.Rating;
import com.ceiba.rating.model.Stars;
import org.springframework.cache.annotation.Cacheable;
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
            throw new AlreadyExistException(String.format("Rating with ID %s already exist", rating.getId()));
        }

        this.ratings.add(rating);
        return rating;
    }

    public Rating updateRating(Long id, Rating rating) {
        if (!anyMatch(id)) {
            throw new NotFoundException(String.format("Rating with ID %s not found", id));
        }

        int index = ratings.indexOf(getById(id));
        this.ratings.set(index, rating);
        return rating;
    }

    @Cacheable("getAllRatings")
    public List<Rating> findAllRatings() {
        return this.ratings;
    }

    public List<Rating> findRatingsByBookId(Long bookId) {
        return ratings.stream().filter(rating -> rating.getBookId() == bookId)
                .collect(Collectors.toList());
    }

    public void deleteRating(Long id) {
        if (!anyMatch(id)) {
            throw new NotFoundException(String.format("Rating with ID %s not found", id));
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
