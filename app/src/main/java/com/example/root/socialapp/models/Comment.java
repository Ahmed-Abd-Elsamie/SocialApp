package com.example.root.socialapp.models;

/**
 * Created by root on 25/08/18.
 */

public class Comment {

    private String name;
    private String img;
    private String date;
    private String comment;


    public Comment(){

    }

    public Comment(String name, String img, String date, String comment) {
        this.name = name;
        this.img = img;
        this.date = date;
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
