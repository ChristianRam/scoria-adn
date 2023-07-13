package com.ceiba.rating.controller;

import com.ceiba.rating.model.Rating;
import com.ceiba.rating.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ratings")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @PostMapping
    public Rating createRating(@RequestBody Rating rating) {
        return ratingService.createRating(rating);
    }

    @PutMapping("/{ratingId}")
    public Rating updateRating(@PathVariable Long ratingId, @RequestBody Rating rating) {
        return ratingService.updateRating(ratingId, rating);
    }

    @GetMapping
    public List<Rating> findRatingsByBookId(
            @RequestParam(required = false, defaultValue = "0") Long bookId) {
        if (bookId.equals(0L)) {
            return ratingService.findAllRatings();
        }
        return ratingService.findRatingsByBookId(bookId);
    }

    @DeleteMapping("/{ratingId}")
    public void deleteRating(@PathVariable Long ratingId) {
        ratingService.deleteRating(ratingId);
    }
}
