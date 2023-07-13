package com.ceiba.book.controller;

import com.ceiba.book.model.Book;
import com.ceiba.book.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping
    public Book createBook(@RequestBody Book book) {
        return bookService.createBook(book);
    }

    @PutMapping("/{bookId}")
    public Book updateBook(@PathVariable Long bookId, @RequestBody Book book) {
        return bookService.updateBook(bookId, book);
    }

    @GetMapping
    public List<Book> findAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{bookId}")
    public Book findBook(@PathVariable Long bookId) {
        return bookService.getBookById(bookId);
    }

    @DeleteMapping("/{bookId}")
    public void deleteBook(@PathVariable Long bookId) {
        bookService.deleteBook(bookId);
    }
}
