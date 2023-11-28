package com.ceiba.rating.controller;

import com.ceiba.rating.model.Rating;
import com.ceiba.rating.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ratings")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @PostMapping
    public ResponseEntity<Rating> createRating(@RequestBody Rating rating) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ratingService.createRating(rating));
    }

    @PutMapping("/{ratingId}")
    public ResponseEntity<Rating> updateRating(@PathVariable Long ratingId, @RequestBody Rating rating) {
        return ResponseEntity.ok(ratingService.updateRating(ratingId, rating));
    }

    @GetMapping
    public ResponseEntity<List<Rating>> findRatingsByBookId(
            @RequestParam(required = false, defaultValue = "0") Long bookId) {
        List<Rating> ratings;

        if (bookId.equals(0L)) {
            ratings = ratingService.findAllRatings();
        } else {
            ratings = ratingService.findRatingsByBookId(bookId);
        }

        return ResponseEntity.ok(ratings);
    }

    @DeleteMapping("/{ratingId}")
    public ResponseEntity<?> deleteRating(@PathVariable Long ratingId) {
        ratingService.deleteRating(ratingId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}
