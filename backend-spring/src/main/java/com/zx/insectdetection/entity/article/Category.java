package com.zx.insectdetection.entity.article;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

    public Category() {
    }

    public Category(Long id, String name, String description) {
        this.id  = id;
        this.name  = name;
        this.description  = description;
    }

}