package com.ceiba.library.feign;

import com.ceiba.library.model.Book;
import com.ceiba.library.model.Rating;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "rating-service")
public interface IRatingFeignClient {

    @GetMapping("/ratings")
    List<Rating> findRatingsByBookId(@RequestParam Long bookId);
}
