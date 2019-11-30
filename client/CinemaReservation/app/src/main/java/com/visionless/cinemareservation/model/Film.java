package com.visionless.cinemareservation.model;


public class Film {
    int id;
    String name;
    String date;
    String director;

    public Film(int id, String name, String date, String director) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.director = director;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

}