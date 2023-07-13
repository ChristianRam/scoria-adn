package com.ceiba.rating.controller;

import com.ceiba.rating.model.Rating;
import com.ceiba.rating.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rating")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @PostMapping
    public String addRating(@RequestBody Rating rating) {
        return ratingService.add(rating);
    }

    @PutMapping("/{id}")
    public String updateRating(@PathVariable Long id, @RequestBody Rating book) {
        return ratingService.update(id, book);
    }

    @GetMapping
    public List<Rating> getAllRatings() {
        return ratingService.getAll();
    }

    @GetMapping("/book-id/{bookId}")
    public List<Rating> getRatingsByBookId(@PathVariable Long bookId) {
        return ratingService.getByBookId(bookId);
    }

    @DeleteMapping("/{id}")
    public String deleteRating(@PathVariable Long id) {
        return ratingService.delete(id);
    }
}
