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


    public String add(Book book) {
        if (anyMatch(book.getId())) {
            return "Error: This book already exist!";
        }

        this.books.add(book);
        return "Book added sucessfully!";
    }

    public String update(Long id, Book book) {
        if (!anyMatch(id)) {
            return String.format("Book with id %s does not exist", id);
        }

        int index = books.indexOf(getById(id));
        this.books.set(index, book);
        return String.format("Book with id %s updated sucessfully", id);
    }

    public List<Book> getAll() {
        return this.books;
    }

    public Book getById(Long id) {
        return books.stream().filter(book -> book.getId() == id).findFirst().orElse(new Book());
    }

    public String delete(Long id) {
        if (!anyMatch(id)) {
            return String.format("Book with id %s does not exist", id);
        }

        this.books.removeIf(book -> book.getId().equals(id));
        return String.format("Book with id %s deleted sucessfully", id);
    }

    private boolean anyMatch(Long id) {
        return books.stream().anyMatch(b -> b.getId().equals(id));
    }
}
