package com.ceiba.library.controller;

import com.ceiba.library.feign.IBookFeignClient;
import com.ceiba.library.feign.IRatingFeignClient;
import com.ceiba.library.model.Book;
import com.ceiba.library.model.Rating;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/library")
@Slf4j
@AllArgsConstructor
public class LibraryController {

    private final IBookFeignClient bookFeignClient;

    private final IRatingFeignClient ratingFeignClient;

    @CircuitBreaker(name="getBook", fallbackMethod = "alternativeBook")
    @GetMapping("/{bookId}")
    public Book getBookWithRatings(@PathVariable Long bookId) {
        Book book = bookFeignClient.findBook(bookId);
        book.setRatings(ratingFeignClient.findRatingsByBookId(bookId));

        return book;
    }

    public Book alternativeBook(Long id, Throwable e) {
        log.info(e.getMessage());

        return Book.builder()
                .id(id)
                .title("The Great Gatsby")
                .author("F. Scott Fitzgerald")
                .ratings(Arrays.asList(Rating.builder().build()))
                .build();
    }
}
