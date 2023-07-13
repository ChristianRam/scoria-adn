package com.ceiba.book.service;

import com.ceiba.book.model.Book;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class BookService {

    private List<Book> books = Stream.of(
            new Book(1L, "Harry Potter", "J. K. Rowling"),
            new Book(2L, "The Lord of the Ring", "John Ronald Reuel Tolkien"),
            new Book(3L, "Jane Eyre", "Charlotte Bronte"),
            new Book(4L, "Wuthering Heights", "Emily Bronte"),
            new Book(5L, "The Great Gatsby", "F. Scott Fitzgerald")
    ).collect(Collectors.toList());


    public Book createBook(Book book) {
        if (anyMatch(book.getId())) {
            return null;
        }

        this.books.add(book);
        return book;
    }

    public Book updateBook(Long id, Book book) {
        if (!anyMatch(id)) {
            return null;
        }

        int index = books.indexOf(getBookById(id));
        this.books.set(index, book);
        return book;
    }

    public List<Book> getAllBooks() {
        return this.books;
    }

    public Book getBookById(Long id) {
        return books.stream().filter(book -> book.getId() == id).findFirst().get();
    }

    public void deleteBook(Long id) {
        if (!anyMatch(id)) {
            return;
        }

        this.books.removeIf(book -> book.getId().equals(id));
    }

    private boolean anyMatch(Long id) {
        return books.stream().anyMatch(b -> b.getId().equals(id));
    }
}
