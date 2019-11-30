package com.visionless.cinemareservation.model;

public class Ticket {
    int seat;
    String film;
    String saloon;
    String date;
    String time;

    public Ticket(int seat, String film, String saloon, String date, String time) {
        this.seat = seat;
        this.film = film;
        this.saloon = saloon;
        this.date = date;
        this.time = time;
    }

    public int getSeat() {
        return seat;
    }

    public void setSeat(int seat) {
        this.seat = seat;
    }

    public String getFilm() {
        return film;
    }

    public void setFilm(String film) {
        this.film = film;
    }

    public String getSaloon() {
        return saloon;
    }

    public void setSaloon(String saloon) {
        this.saloon = saloon;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}