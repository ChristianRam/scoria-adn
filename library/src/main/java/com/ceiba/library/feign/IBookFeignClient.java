package com.ceiba.library.feign;

import com.ceiba.library.model.Book;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "book-service")
public interface IBookFeignClient {

    @GetMapping("/books/{bookId}")
    Book findBook(@PathVariable Long bookId);
}
