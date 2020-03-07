package com.iblog.root.socialapp.models;

/**
 * Created by root on 17/08/18.
 */

public class Notification_item {

    private String id;
    private String name;
    private String img;

    public Notification_item(){

    }


    public Notification_item(String id, String name, String img) {
        this.id = id;
        this.name = name;
        this.img = img;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
