package com.example.qls.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String author;

    private String category;

    @Column(name = "release_date")
    private Date releaseDate;

    @Column(name = "page_count")
    private int pageCount;

    @Column(name = "sold_count")
    private int soldCount;

    @Column(name = "cover_image")
    private String coverImage;
}
