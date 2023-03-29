package com.catalog.films.model;

import lombok.Data;

@Data
public class Film {

    int id;
    String name;
    String year;
    String rating;
    String genre;
    String producer;
    String actor;
    String feedBack;

    public Film(int id, String name, String rating, String genre, String producer, String year, String actor) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.rating = rating;
        this.genre = genre;
        this.producer = producer;
        this.actor = actor;
    }

    public Film(String name, String rating, String genre, String producer, String year, String actor) {
        this.name = name;
        this.year = year;
        this.rating = rating;
        this.genre = genre;
        this.producer = producer;
        this.actor = actor;
    }
}
