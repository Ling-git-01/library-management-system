package com.example.backend.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "books")
public class Book  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 100)
    private String author;

    @Column(length = 20)
    private String isbn;

    @Column(length = 100)
    private String publisher;

    @Column(name = "publish_year")
    private Integer publishYear;

    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "total_copies", nullable = false)
    private Integer totalCopies = 1;

    @Column(name = "available_copies", nullable = false)
    private Integer availableCopies = 1;

    @Column(length = 50)
    private String location;

    @Column(name = "cover_url", length = 300)
    private String coverUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer status = 1;

    // 关联分类实体（可选，简化查询）
    @ManyToOne
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private BookCategory category;
}
