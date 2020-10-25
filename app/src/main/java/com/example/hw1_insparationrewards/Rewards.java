package com.example.hw1_insparationrewards;

import java.io.Serializable;

public class Rewards implements Serializable {
    private String date;
    private String name;
    private int points;
    private String comment;


    public Rewards(String date, String name, int points, String comment) {
        this.date = date;
        this.name = name;
        this.points = points;
        this.comment = comment;
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

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
