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


    public String add(Rating rating) {
        if (anyMatch(rating.getId())) {
            return "Error: This rating already exist!";
        }

        this.ratings.add(rating);
        return "Rating added sucessfully!";
    }

    public String update(Long id, Rating rating) {
        if (!anyMatch(id)) {
            return String.format("Rating with id %s does not exist", id);
        }

        int index = ratings.indexOf(getById(id));
        this.ratings.set(index, rating);
        return String.format("Rating with id %s updated sucessfully", id);
    }

    public List<Rating> getAll() {
        return this.ratings;
    }

    public List<Rating> getByBookId(Long bookId) {
        return ratings.stream().filter(rating -> rating.getBookId() == bookId).collect(Collectors.toList());
    }

    public String delete(Long id) {
        if (!anyMatch(id)) {
            return String.format("Rating with id %s does not exist", id);
        }

        this.ratings.removeIf(rating -> rating.getId().equals(id));
        return String.format("Rating with id %s deleted sucessfully", id);
    }

    private boolean anyMatch(Long id) {
        return ratings.stream().anyMatch(r -> r.getId().equals(id));
    }

    private Rating getById(Long id) {
        return ratings.stream().filter(rating -> rating.getId() == id)
                .findFirst().orElse(new Rating());
    }
}
