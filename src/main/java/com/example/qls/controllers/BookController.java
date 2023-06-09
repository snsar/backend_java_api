package com.example.qls.controllers;

import com.example.qls.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.example.qls.models.Book;


@RestController
@CrossOrigin
@RequestMapping("/api/books")
public class BookController {
    @Autowired
    private BookRepository bookRepository;

    @GetMapping
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable("id") Long id) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        return optionalBook.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Book> addBook(@RequestParam("coverImage") MultipartFile coverImageFile,
                                        @RequestParam("title") String title,
                                        @RequestParam("author") String author,
                                        @RequestParam("category") String category,
                                        @RequestParam("releaseDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date releaseDate,
                                        @RequestParam("pageCount") int pageCount,
                                        @RequestParam("soldCount") int soldCount) {
        try {
            Book book = new Book();
            book.setTitle(title);
            book.setAuthor(author);
            book.setCategory(category);
            book.setReleaseDate(releaseDate);
            book.setPageCount(pageCount);
            book.setSoldCount(soldCount);

            // Save the cover image file
            if (coverImageFile != null && !coverImageFile.isEmpty()) {
                try {
                    // Create the images directory if it does not exist
                    Path path = Paths.get("./images");
                    if (!Files.exists(path)) {
                        Files.createDirectories(path);
                    }

                    // Save the cover image file to the images directory
                    String filename = StringUtils.cleanPath(coverImageFile.getOriginalFilename());
                    Path filePath = path.resolve(filename);
                    Files.copy(coverImageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                    // Set the cover image file path in the book object
                    book.setCoverImage(filePath.toString());
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            }

            // Save the book to the database
            bookRepository.save(book);

            // Return a success response
            return ResponseEntity.status(HttpStatus.CREATED).body(book);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable("id") Long id,
                                           @RequestParam(value = "coverImage", required = false) MultipartFile coverImageFile,
                                           @RequestParam("title") String title,
                                           @RequestParam("author") String author,
                                           @RequestParam("category") String category,
                                           @RequestParam("releaseDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date releaseDate,
                                           @RequestParam("pageCount") int pageCount,
                                           @RequestParam("soldCount") int soldCount) {
        try {
            Optional<Book> optionalBook = bookRepository.findById(id);
            if (!optionalBook.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Book book = optionalBook.get();
            book.setTitle(title);
            book.setAuthor(author);
            book.setCategory(category);
            book.setReleaseDate(releaseDate);
            book.setPageCount(pageCount);
            book.setSoldCount(soldCount);

            // Update the cover image file
            if (coverImageFile != null && !coverImageFile.isEmpty()) {
                try {
                    // Create the uploads directory if it does not exist
                    Path path = Paths.get("./uploads");
                    if (!Files.exists(path)) {
                        Files.createDirectories(path);
                    }

                    // Save the cover image file to the uploads directory
                    String filename = StringUtils.cleanPath(coverImageFile.getOriginalFilename());
                    Path filePath = path.resolve(filename);
                    Files.copy(coverImageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                    // Set the cover image file path in the book object
                    book.setCoverImage(filePath.toString());
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            }

            // Save the updated book to the database
            Book updatedBook = bookRepository.save(book);

            // Return a success response
            return ResponseEntity.ok(updatedBook);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable("id") Long id) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            bookRepository.delete(book);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
