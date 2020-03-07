package com.iblog.root.socialapp.models;

public class Conversation {

    private User user;
    private String lastMsg;
    private String date;
    private int num;

    public Conversation() {

    }

    public Conversation(User user, String lastMsg, String date, int num) {
        this.user = user;
        this.lastMsg = lastMsg;
        this.date = date;
        this.num = num;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
